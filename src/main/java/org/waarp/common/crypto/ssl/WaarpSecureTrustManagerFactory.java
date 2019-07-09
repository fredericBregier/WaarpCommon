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
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Waarp . If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.crypto.ssl;

import org.waarp.common.exception.CryptoException;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import java.security.KeyStore;

/**
 * A SecureTrustManagerFactory
 *
 * @author Frederic Bregier
 *
 */
public class WaarpSecureTrustManagerFactory extends TrustManagerFactorySpi {
    private final WaarpX509TrustManager ggTrustManager;

    private final TrustManager[] trustManager;

    private final boolean needAuthentication;
    private final boolean hasTrustStore;

    /**
     * Accept all connections
     *
     */
    public WaarpSecureTrustManagerFactory() {
        ggTrustManager = new WaarpX509TrustManager();
        trustManager = new TrustManager[] {
                ggTrustManager
        };
        needAuthentication = false;
        hasTrustStore = false;
    }

    /**
     *
     * @param tmf
     * @param clientAuthent
     *            True if the TrustStore is used for Client Authentication
     * @throws CryptoException
     */
    public WaarpSecureTrustManagerFactory(TrustManagerFactory tmf,
                                          boolean clientAuthent) throws CryptoException {
        ggTrustManager = new WaarpX509TrustManager(tmf);
        trustManager = new TrustManager[] {
                ggTrustManager
        };
        needAuthentication = clientAuthent;
        hasTrustStore = true;
    }

    /**
     *
     * @return True if this TrustManager really check authentication
     */
    public boolean hasTrustStore() {
        return hasTrustStore;
    }

    /**
     *
     * @return True if this TrustManager really check authentication
     */
    public boolean needAuthentication() {
        return needAuthentication;
    }

    /**
     *
     * @return The TrustManager arrays
     */
    public TrustManager[] getTrustManagers() {
        return trustManager.clone();
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return getTrustManagers();
    }

    @Override
    protected void engineInit(KeyStore arg0) {
        // Unused
    }

    @Override
    protected void engineInit(ManagerFactoryParameters arg0) {
        // Unused
    }

}
