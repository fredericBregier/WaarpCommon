/**
 * This file is part of Waarp Project.
 * 
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Waarp . If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.crypto;

/**
 * This class implements a simple Key Manager for HmacSha1 class from name
 * 
 * @author frederic bregier
 */
public class HmacSha256Manager extends KeyManager {
    public static final HmacSha256Manager aesManager = new HmacSha256Manager();

    /*
     * (non-Javadoc)
     * @see atlas.cryptage.KeyManager#createKeyObject()
     */
    @Override
    public KeyObject createKeyObject() {
        return new HmacSha256();
    }
}
