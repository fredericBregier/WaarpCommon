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
package org.waarp.common.crypto;

import org.junit.Test;
import org.waarp.common.crypto.DynamicKeyObject.INSTANCES;
import org.waarp.common.crypto.DynamicKeyObject.INSTANCESMAX;

import static org.junit.Assert.*;

/**
 * @author "Frederic Bregier"
 *
 */
public class KeyTest {

    /**
     * test function
     *
     * @param plaintext
     * @param size
     * @param algo
     * @throws Exception
     */
    private static void test(String plaintext, int size, String algo)
            throws Exception {
        DynamicKeyObject dyn = new DynamicKeyObject(size, algo, algo, algo);
        // Generate a key
        dyn.generateKey();
        // get the generated key
        byte[] secretKey = dyn.getSecretKeyInBytes();
        // crypt one text
        byte[] ciphertext = dyn.crypt(plaintext);
        // Test the set Key
        dyn.setSecretKey(secretKey);
        // decrypt the cipher
        String plaintext2 = dyn.decryptInString(ciphertext);
        // print the result
        assertArrayEquals(plaintext.getBytes(), plaintext2.getBytes());

        // same on String only
        int nb = 1000;
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < nb; i++) {
            String cipherString = dyn.cryptToHex(plaintext);
            // System.out.println("cipherString = " + cipherString);
            String plaintext3 = dyn.decryptHexInString(cipherString);
            assertArrayEquals(plaintext.getBytes(), plaintext3.getBytes());
        }
        long time2 = System.currentTimeMillis();
        System.out.println(algo + ": Total time: " + (time2 - time1) + " ms, " +
                           (nb * 1000 / (time2 - time1)) + " crypt or decrypt/s");
    }

    /**
     * Test method
     */
    @Test
    public void testToCrypt() {
        String plaintext =
                "This is a try for a very long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long String";
        // Can implements with KeyGenerator AES, ARCFOUR, Blowfish, DES, DESede,
        // RC2, RC4
        for (INSTANCES instance : INSTANCES.values()) {
            try {
                test(plaintext, instance.size, instance.name());
            } catch (Exception e) {
                fail(e.getMessage());
                return;
            }
        }
        for (INSTANCESMAX instance : INSTANCESMAX.values()) {
            try {
                test(plaintext, instance.size, instance.name());
            } catch (Exception e) {
                fail(e.getMessage());
                return;
            }
        }
    }
}
