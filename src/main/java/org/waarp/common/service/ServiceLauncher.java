/**
 * This file is part of Waarp Project.
 * <p>
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author tags. See the COPYRIGHT.txt in the
 * distribution for a full listing of individual contributors.
 * <p>
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Waarp .  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.service;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.waarp.common.logging.WaarpLogger;
import org.waarp.common.logging.WaarpLoggerFactory;
import org.waarp.common.logging.WaarpSlf4JLoggerFactory;
import org.waarp.common.utility.WaarpThreadFactory;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Launch the Engine from a variety of sources, either through a main() or invoked through Apache Daemon.
 *
 * @author Frederic Bregier Inspired from Apache Daemon Wiki
 */
public abstract class ServiceLauncher implements Daemon {
    /**
     * Internal Logger
     */
    protected static WaarpLogger logger;

    protected static EngineAbstract engine = null;

    protected static ServiceLauncher engineLauncherInstance = null;
    protected static DaemonController controller = null;
    protected static boolean stopCalledCorrectly = false;
    protected ExecutorService executor = null;

    public ServiceLauncher() {
        if (logger == null) {
            logger = WaarpLoggerFactory.getLogger(ServiceLauncher.class);
        }
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor(new WaarpThreadFactory("ServiceLauncher"));
        }
        engineLauncherInstance = this;
        if (engine == null) {
            engine = getNewEngineAbstract();
        }
    }

    protected static void initStatic() {
        if (!(WaarpLoggerFactory.getDefaultFactory() instanceof WaarpSlf4JLoggerFactory)) {
            WaarpLoggerFactory.setDefaultFactory(new WaarpSlf4JLoggerFactory(null));
        }
        if (logger == null) {
            logger = WaarpLoggerFactory.getLogger(ServiceLauncher.class);
        }
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        ;
        logger.debug("Engine " + className);
        try {
            engineLauncherInstance = (ServiceLauncher) Class.forName(className).newInstance();
        } catch (Throwable e) {
            logger.error("Engine not correctly initialized", e);
            System.exit(2);
        }
        if (engineLauncherInstance == null || engine == null) {
            logger.error("Engine not correctly initialized");
            System.exit(1);
        }
    }

    /**
     * The Java entry point.
     *
     * @param args Command line arguments, all ignored.
     */
    public static void _main(String[] args) {
        initStatic();
        // the main routine is only here so I can also run the app from the command line
        engineLauncherInstance.initialize();

        Scanner sc = new Scanner(System.in);
        // wait until receive stop command from keyboard
        System.out.printf("Enter 'stop' to halt: ");
        while (!sc.nextLine().toLowerCase().equals("stop")) {
            ;
        }

        if (!engine.isShutdown()) {
            engineLauncherInstance.terminate();
        }
        sc.close();
    }

    /**
     * Windows mode<br>
     * <br>
     * Static methods called by prunsrv to start/stop the Windows service. Pass the argument "start" to start the
     * service, and pass "stop" to stop the service.
     *
     * <pre>
     * prunsrv.exe //IS/MyService --Classpath=C:\...\xxx.jar --Description=&quot;My Java Service&quot; --Jvm=auto --StartMode=jvm --StartClass=org.waarp.xxx.service.ServiceLauncher --StartMethod=windowsService --StartParams=start --StopMode=jvm --StopClass=org.waarp.xxx.service.ServiceLauncher --StopMethod=windowsService --StopParams=stop
     * </pre>
     *
     * @param args Arguments from prunsrv command line
     *
     * @throws Exception
     **/
    public static void _windowsService(String args[]) throws Exception {
        initStatic();
        String cmd = "start";
        if (args.length > 0) {
            cmd = args[0];
        }
        if ("start".equals(cmd)) {
            engineLauncherInstance.windowsStart();
        } else {
            engineLauncherInstance.windowsStop();
        }
    }

    /**
     * Windows mode<br>
     * <br>
     * Static methods called by prunsrv to start the Windows service.
     *
     * <pre>
     * prunsrv.exe //IS/MyService --Classpath=C:\...\xxx.jar --Description=&quot;My Java Service&quot; --Jvm=auto --StartMode=jvm --StartClass=org.waarp.xxx.service.ServiceLauncher --StartMethod=windowsStart --StopMode=jvm --StopClass=org.waarp.xxx.service.ServiceLauncher --StopMethod=windowsStop
     * </pre>
     *
     * @param args Arguments are ignored
     *
     * @throws Exception
     **/
    public static void _windowsStart(String args[]) throws Exception {
        initStatic();
        engineLauncherInstance.windowsStart();
    }

    /**
     * Windows mode<br>
     * <br>
     * Static methods called by prunsrv to stop the Windows service.
     *
     * <pre>
     * prunsrv.exe //IS/MyService --Classpath=C:\...\xxx.jar --Description=&quot;My Java Service&quot; --Jvm=auto --StartMode=jvm --StartClass=org.waarp.xxx.service.ServiceLauncher --StartMethod=windowsStart --StopMode=jvm --StopClass=org.waarp.xxx.service.ServiceLauncher --StopMethod=windowsStop
     * </pre>
     *
     * @param args Arguments are ignored
     **/
    public static void _windowsStop(String args[]) {
        initStatic();
        stopCalledCorrectly = true;
        engineLauncherInstance.windowsStop();
    }

    /**
     * @return a new EngineAbstract
     */
    protected abstract EngineAbstract getNewEngineAbstract();

    /**
     * Internal command
     *
     * @throws Exception
     */
    protected void windowsStart() throws Exception {
        logger.info("windowsStart called");
        initialize();
        // We must wait in Windows Mode
        boolean status = false;
        try {
            status = engine.waitShutdown();
        } catch (InterruptedException e) {
        }
        if (!status || !stopCalledCorrectly) {
            // Was stopped outside service management
            terminate();
            if (controller != null) {
                controller.fail("Service stopped abnormally");
            } else {
                throw new Exception("Service stopped abnormally");
            }
        }
    }

    /**
     * Internal command
     */
    protected void windowsStop() {
        logger.info("windowsStop called from Service: " + stopCalledCorrectly);
        terminate();
        // should we force Future to be cancelled there?
    }

    // Implementing the Daemon interface is not required for Windows but is for Linux
    public void init(DaemonContext arg0) throws Exception {
        controller = arg0.getController();
        logger.info("Daemon init");
    }

    public void start() {
        logger.info("Daemon start");
        initialize();
    }

    public void stop() {
        logger.info("Daemon stop");
        terminate();
    }

    public void destroy() {
        logger.info("Daemon destroy");
        terminate();
    }

    /**
     * Do the work of starting the engine
     */
    protected void initialize() {
        if (engine != null) {
            logger.info("Starting the Engine");
            engine.setDaemon(true);
            executor.execute(engine);
        } else {
            logger.error("Engine cannot be started since it is not initialized");
        }
    }

    /**
     * Cleanly stop the engine.
     */
    protected void terminate() {
        if (engine != null) {
            logger.info("Stopping the Engine");
            engine.shutdown();
            engine = null;
        }
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        logger.info("Engine stopped");
    }
}