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

import java.io.File;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import org.waarp.common.exception.CryptoException;

/**
 * This class handles methods to crypt (not decrypt) messages with HmacSha256 algorithm (very efficient:
 * 105000/s).<br>
 * <br>
 * Usage:<br>
 * <ul>
 * <li>Create a HmacSha256 object: HmacSha256 key = new HmacSha256();</li>
 * <li>Create a key:
 * <ul>
 * <li>Generate: key.generateKey();<br>
 * The method key.getSecretKeyInBytes() allow getting the key in Bytes.</li>
 * <li>From an external source: key.setSecretKey(arrayOfBytes);</li>
 * </ul>
 * </li>
 * <li>To crypt a String in a Base64 format: String myStringCrypt = key.cryptToString(myString);</li>
 * </ul>
 * 
 * @author frederic bregier
 * 
 */
public class HmacSha256 extends KeyObject {
    public final static int KEY_SIZE = 128;
    public final static String ALGO = "HmacSHA256";
    public final static String INSTANCE = ALGO;
    public final static String EXTENSION = "hs2";

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

    /* (non-Javadoc)
     * @see org.waarp.common.crypto.KeyObject#toCrypt()
     */
    @Override
    public Cipher toCrypt() {
        throw new IllegalArgumentException("Cannot be used for HmacSha256");
    }

    /* (non-Javadoc)
     * @see org.waarp.common.crypto.KeyObject#crypt(byte[])
     */
    @Override
    public byte[] crypt(byte[] plaintext) throws Exception {
        Mac mac = Mac.getInstance(ALGO);
        mac.init(secretKey);
        return mac.doFinal(plaintext);
    }

    /* (non-Javadoc)
     * @see org.waarp.common.crypto.KeyObject#toDecrypt()
     */
    @Override
    public Cipher toDecrypt() {
        throw new IllegalArgumentException("Cannot be used for HmacSha256");
    }

    /* (non-Javadoc)
     * @see org.waarp.common.crypto.KeyObject#decrypt(byte[])
     */
    @Override
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        throw new IllegalArgumentException("Cannot be used for HmacSha256");
    }

    /**
     * Generates a HmacSha256 key and saves it into the file given as argument
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Filename is needed as argument");
        }
        HmacSha256 key = new HmacSha256();
        try {
            key.generateKey();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
        try {
            key.saveSecretKey(new File(args[0]));
        } catch (CryptoException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
        System.out.println("New HmacSha256 key file is generated: " + args[0]);
    }
}
