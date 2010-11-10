/**
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3.0 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package goldengate.common.crypto.ssl;

import java.security.Security;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.handler.ssl.SslHandler;

import goldengate.common.logging.GgInternalLogger;
import goldengate.common.logging.GgInternalLoggerFactory;

/**
 * SSL ContextFactory for Netty.
 *
 * @author Frederic Bregier
 *
 */
public class GgSslContextFactory {
    /**
     * Internal Logger
     */
    private static final GgInternalLogger logger = GgInternalLoggerFactory
            .getLogger(GgSslContextFactory.class);

    /**
    *
    */
    private static final String PROTOCOL = "TLS";

    /**
    *
    */
    private final SSLContext SERVER_CONTEXT;

    /**
    *
    */
    private final SSLContext CLIENT_CONTEXT;

    private boolean needClientAuthentication = false;

    /**
     * Create both CONTEXT
     * @param ggSecureKeyStore
     */
    public GgSslContextFactory(GgSecureKeyStore ggSecureKeyStore) {
        // Both construct Client and Server mode
        SERVER_CONTEXT = initSslContextFactory(ggSecureKeyStore, true);
        CLIENT_CONTEXT = initSslContextFactory(ggSecureKeyStore, false);
    }

    /**
     * Create only one of the CONTEXT
     * @param ggSecureKeyStore
     * @param serverMode
     */
    public GgSslContextFactory(GgSecureKeyStore ggSecureKeyStore, boolean serverMode) {
        if (serverMode) {
            SERVER_CONTEXT = initSslContextFactory(ggSecureKeyStore, serverMode);
            CLIENT_CONTEXT = null;
        } else {
            CLIENT_CONTEXT = initSslContextFactory(ggSecureKeyStore, serverMode);
            SERVER_CONTEXT = null;
        }
    }
    /**
     *
     * @param ggSecureKeyStore
     * @param serverMode
     * @return the SSLContext
     */
    private SSLContext initSslContextFactory(GgSecureKeyStore ggSecureKeyStore, boolean serverMode) {
        String algorithm = Security
                .getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        SSLContext serverContext = null;
        SSLContext clientContext = null;
        if (serverMode) {
            try {
                // Initialize the SSLContext to work with our key managers.
                serverContext = SSLContext.getInstance(PROTOCOL);
                GgSecureTrustManagerFactory secureTrustManagerFactory =
                    ggSecureKeyStore.getSecureTrustManagerFactory();
                needClientAuthentication = secureTrustManagerFactory.needAuthentication();
                if (secureTrustManagerFactory.hasTrustStore()) {
                    logger.debug("Has TrustManager");
                    serverContext.init(ggSecureKeyStore.getKeyManagerFactory().getKeyManagers(),
                        secureTrustManagerFactory.getTrustManagers(), null);
                } else {
                    logger.debug("No TrustManager");
                    serverContext.init(ggSecureKeyStore.getKeyManagerFactory().getKeyManagers(),
                            null, null);
                }
                return serverContext;
            } catch (Exception e) {
                logger.error("Failed to initialize the server-side SSLContext", e);
                throw new Error("Failed to initialize the server-side SSLContext",
                        e);
            }
        } else {
            try {
                clientContext = SSLContext.getInstance(PROTOCOL);
                GgSecureTrustManagerFactory secureTrustManagerFactory =
                    ggSecureKeyStore.getSecureTrustManagerFactory();
                needClientAuthentication = secureTrustManagerFactory.needAuthentication();
                if (secureTrustManagerFactory.hasTrustStore()) {
                    logger.debug("Has TrustManager");
                    clientContext.init(ggSecureKeyStore.getKeyManagerFactory().getKeyManagers(),
                            secureTrustManagerFactory.getTrustManagers(), null);
                } else {
                    logger.debug("No TrustManager");
                    clientContext.init(ggSecureKeyStore.getKeyManagerFactory().getKeyManagers(),
                            null, null);
                }
                return clientContext;
            } catch (Exception e) {
                logger.error("Failed to initialize the client-side SSLContext", e);
                throw new Error("Failed to initialize the client-side SSLContext",
                        e);
            }
        }
    }

    /**
     * @return the Server Context
     */
    public SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    /**
     * @return the Client Context
     */
    public SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }
    /**
     * To be called before adding as first entry in the PipelineFactory as<br>
     * pipeline.addLast("ssl", sslhandler);<br>
     *
     * @param serverMode True if in Server Mode, else False in Client mode
     * @param needClientAuth True if the client needs to be authenticated (only if serverMode is True)
     * @param renegotiationEnable True if you want to enable renegotiation (security issue CVE-2009-3555)
     * @param executorService if not Null, gives a specific executorService
     * @return the sslhandler
     */
    public SslHandler initPipelineFactory(boolean serverMode,
            boolean needClientAuth, boolean renegotiationEnable,
            ExecutorService executorService) {
        // Add SSL handler first to encrypt and decrypt everything.
        SSLEngine engine;
        logger.debug("Has TrustManager? "+needClientAuth+" Is ServerMode? "+serverMode);
        if (serverMode) {
            engine = getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            engine.setNeedClientAuth(needClientAuth);
        } else {
            engine = getClientContext().createSSLEngine();
            engine.setUseClientMode(true);
        }
        SslHandler handler = null;
        if (executorService != null) {
            handler = new SslHandler(engine, executorService);
        } else {
            handler = new SslHandler(engine);
        }
        // Set the RenegotiationEnable or not
        handler.setEnableRenegotiation(renegotiationEnable);
        return handler;
    }
    /**
     *
     * @return True if the associated KeyStore has a TrustStore
     */
    public boolean needClientAuthentication() {
        return needClientAuthentication;
    }
}
