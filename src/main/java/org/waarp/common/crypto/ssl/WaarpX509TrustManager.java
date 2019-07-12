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

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Waarp X509 Trust Manager implementation
 *
 * @author Frederic Bregier
 */
public class WaarpX509TrustManager implements X509TrustManager {
    /**
     * First using default X509TrustManager returned by the global TrustManager. Then delegate decisions to it, and fall
     * back to the logic in this class if the default doesn't trust it.
     */
    private X509TrustManager defaultX509TrustManager = null;

    /**
     * Create an "always-valid" X509TrustManager
     */
    public WaarpX509TrustManager() {
        defaultX509TrustManager = null;
    }

    /**
     * Create a "default" X509TrustManager
     *
     * @param tmf
     *
     * @throws CryptoException
     */
    public WaarpX509TrustManager(TrustManagerFactory tmf) throws CryptoException {
        TrustManager[] tms = tmf.getTrustManagers();
        /**
         * Iterate over the returned trustmanagers, look for an instance of X509TrustManager and use
         * it as the default
         */
        for (int i = 0; i < tms.length; i++) {
            if (tms[i] instanceof X509TrustManager) {
                defaultX509TrustManager = (X509TrustManager) tms[i];
                return;
            }
        }
        /**
         * Could not initialize, maybe try to build it from scratch?
         */
        throw new CryptoException("Cannot initialize the WaarpX509TrustManager");
    }

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
        if (defaultX509TrustManager == null) {
            return; // valid
        }
        defaultX509TrustManager.checkClientTrusted(arg0, arg1);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
        if (defaultX509TrustManager == null) {
            return; // valid
        }
        defaultX509TrustManager.checkServerTrusted(arg0, arg1);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (defaultX509TrustManager == null) {
            return new X509Certificate[0]; // none valid
        }
        return defaultX509TrustManager.getAcceptedIssuers();
    }

}
