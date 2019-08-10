/*
 * This file is part of Waarp Project (named also Waarp or GG).
 *
 *  Copyright (c) 2019, Waarp SAS, and individual contributors by the @author
 *  tags. See the COPYRIGHT.txt in the distribution for a full listing of
 *  individual contributors.
 *
 *  All Waarp Project is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at your
 *  option) any later version.
 *
 *  Waarp is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 *  A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  Waarp . If not, see <http://www.gnu.org/licenses/>.
 */

package org.waarp.common.utility;

import org.waarp.common.utility.WaarpShutdownHook.ShutdownConfiguration;

public final class ToRun extends Thread {
  public static ToRunShutdownHook toRunShutdownHook;

  public static class ToRunShutdownHook extends WaarpShutdownHook {

    public ToRunShutdownHook(final ShutdownConfiguration configuration) {
      super(configuration);
    }

    @Override
    protected void exit() {
      System.out.println(
          "Interruption on going for PID: " + UUID.jvmProcessId());
    }
  }

  public static void main(String[] args) throws InterruptedException {
    System.out.println("PID is " + UUID.jvmProcessId());
    ShutdownConfiguration shutdownConfiguration = new ShutdownConfiguration();
    shutdownConfiguration.timeout = 100;
    toRunShutdownHook =
        new ToRunShutdownHook(shutdownConfiguration);
    toRunShutdownHook.setDaemon(true);
    Runtime.getRuntime().addShutdownHook(toRunShutdownHook);
    Thread.sleep(100000);
  }

  @Override
  public void run() {
    try {
      main(new String[0]);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
