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
 * This class handles methods to crypt and decrypt messages with DES algorithm (very efficient:
 * 40000/s).<br>
 * <br>
 * Usage:<br>
 * <ul>
 * <li>Create a Des object: Des key = new Des();</li>
 * <li>Create a key:
 * <ul>
 * <li>Generate: key.generateKey();<br>
 * The method key.getSecretKeyInBytes() allow getting the key in Bytes.</li>
 * <li>From an external source: key.setSecretKey(arrayOfBytes);</li>
 * </ul>
 * </li>
 * <li>To crypt a String in a Base64 format: String myStringCrypt = key.cryptToString(myString);</li>
 * <li>To decrypt one string from Base64 format to the original String: String myStringDecrypt =
 * key.decryptStringInString(myStringCrypte);</li>
 * </ul>
 * 
 * @author frederic bregier
 * 
 */
public class Des extends KeyObject {
    /**
     * This value could be between 32 and 128 due to license limitation.
     */
    public final static int KEY_SIZE = 56; // [32..448]
    public final static String ALGO = "DES";
    public final static String INSTANCE = "DES/ECB/PKCS5Padding";
    public final static String EXTENSION = "des";

    /*
     * (non-Javadoc)
     * @see atlas.cryptage.KeyObject#getAlgorithm()
     */
    @Override
    public String getAlgorithm() {
        return ALGO;
    }

    /*
     * (non-Javadoc)
     * @see atlas.cryptage.KeyObject#getInstance()
     */
    @Override
    public String getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     * @see atlas.cryptage.KeyObject#getKeySize()
     */
    @Override
    public int getKeySize() {
        return KEY_SIZE;
    }
}
