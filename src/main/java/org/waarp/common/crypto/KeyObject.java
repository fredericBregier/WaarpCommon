/*******************************************************************************
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
 ******************************************************************************/
package org.waarp.common.crypto;

import org.waarp.common.digest.FilesystemBasedDigest;
import org.waarp.common.exception.CryptoException;
import org.waarp.common.logging.WaarpLogger;
import org.waarp.common.logging.WaarpLoggerFactory;
import org.waarp.common.utility.WaarpStringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;

/**
 * This class handles method to crypt and decrypt using the chosen
 * algorithm.<br>
 *
 * <br>
 * Usage:<br>
 * <ul>
 * <li>Create a Key object: KeyObject key = new KeyObject();</li>
 * <li>Create a key:
 * <ul>
 * <li>Generate: key.generateKey();<br>
 * The method key.getSecretKeyInBytes() allow getting the key in Bytes.</li>
 * <li>From an external source: key.setSecretKey(arrayOfBytes);</li>
 * </ul>
 * </li>
 * <li>To crypt a String in a Hex format: String myStringCrypt =
 * key.cryptToHex(myString);</li>
 * <li>To decrypt one string from Hex format to the original String: String
 * myStringDecrypt =
 * key.decryptHexInString(myStringCrypte);</li>
 * </ul>
 *
 * @author frederic bregier
 */
public abstract class KeyObject {
  /**
   * Internal Logger
   */
  private static final WaarpLogger logger = WaarpLoggerFactory
      .getLogger(KeyObject.class);

  /**
   * The True Key associated with this object
   */
  Key secretKey = null;

  /**
   * Empty constructor
   */
  public KeyObject() {
  }

  /**
   * @return the filename extension to use for this kind of key
   */
  public abstract String getFileExtension();

  /**
   * @return the key associated with this object
   */
  public Key getSecretKey() {
    return secretKey;
  }

  /**
   * Set the secretKey
   *
   * @param secretKey
   */
  public void setSecretKey(Key secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Reconstruct a key from an array of bytes
   */
  public void setSecretKey(byte[] keyData) {
    secretKey = new SecretKeySpec(keyData, getAlgorithm());
  }

  /**
   * Create a Key from a File
   *
   * @param file
   *
   * @throws CryptoException
   * @throws IOException
   */
  public void setSecretKey(File file) throws CryptoException, IOException {
    if (file.canRead()) {
      int len = (int) file.length();
      byte[] key = new byte[len];
      FileInputStream inputStream = null;
      inputStream = new FileInputStream(file);
      DataInputStream dis = new DataInputStream(inputStream);
      try {
        dis.readFully(key);
      } finally {
        dis.close();
      }
      this.setSecretKey(key);
    } else {
      throw new CryptoException("Cannot read crypto file: " + file);
    }
  }

  /**
   * @return the algorithm used (Java name)
   */
  public abstract String getAlgorithm();

  /**
   * Save a Key to a File
   *
   * @param file
   *
   * @throws CryptoException
   * @throws IOException
   */
  public void saveSecretKey(File file) throws CryptoException, IOException {
    if (keyReady() && ((!file.exists()) || file.canWrite())) {
      byte[] key = getSecretKeyInBytes();
      FileOutputStream outputStream = new FileOutputStream(file);
      try {
        outputStream.write(key);
        outputStream.flush();
      } finally {
        outputStream.close();
      }
    } else {
      throw new CryptoException("Cannot read crypto file");
    }
  }

  /**
   * @return True if this key is ready to be used
   */
  public boolean keyReady() {
    return secretKey != null;
  }

  /**
   * Returns the key as an array of bytes in order to be stored somewhere else
   * and retrieved using the
   * setSecretKey(byte[] keyData) method.
   *
   * @return the key as an array of bytes (or null if not ready)
   */
  public byte[] getSecretKeyInBytes() {
    if (keyReady()) {
      return secretKey.getEncoded();
    } else {
      return null;
    }
  }

  /**
   * Generate a key from nothing
   *
   * @throws Exception
   */
  public void generateKey() throws Exception {
    try {
      KeyGenerator keyGen = KeyGenerator.getInstance(getAlgorithm());
      keyGen.init(getKeySize());
      secretKey = keyGen.generateKey();
    } catch (Exception e) {
      logger.warn("GenerateKey Error", e);
      throw e;
    }
  }

  /**
   * @return the size for the algorithm key
   */
  public abstract int getKeySize();

  /**
   * Returns a cipher for encryption associated with the key
   *
   * @return the cipher for encryption or null if it fails in case Encryption
   *     method or key is incorrect
   */
  public Cipher toCrypt() {
    Cipher cipher;
    try {
      cipher = Cipher.getInstance(getInstance());
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    } catch (Exception e) {
      logger.warn("Crypt Error", e);
      return null;
    }
    return cipher;
  }

  /**
   * @return the instance used (Java name)
   */
  public abstract String getInstance();

  /**
   * Crypt one String and returns the crypted array of bytes
   *
   * @param plaintext
   *
   * @return the crypted array of bytes
   *
   * @throws Exception
   */
  public byte[] crypt(String plaintext) throws Exception {
    return crypt(plaintext.getBytes(WaarpStringUtils.UTF8));
  }

  /**
   * Crypt one array of bytes and returns the crypted array of bytes
   *
   * @param plaintext
   *
   * @return the crypted array of bytes
   *
   * @throws Exception
   */
  public byte[] crypt(byte[] plaintext) throws Exception {
    if (!keyReady()) {
      throw new CryptoException("Key not Ready");
    }
    try {
      Cipher cipher = Cipher.getInstance(getInstance());
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      return cipher.doFinal(plaintext);
    } catch (Exception e) {
      logger.warn("Crypt Error", e);
      throw e;
    }
  }

  /**
   * Crypt one String and returns the crypted String as HEX format
   *
   * @param plaintext
   *
   * @return the crypted String as HEX format
   *
   * @throws Exception
   */
  public String cryptToHex(String plaintext) throws Exception {
    return cryptToHex(plaintext.getBytes(WaarpStringUtils.UTF8));
  }

  /**
   * Crypt one array of bytes and returns the crypted String as HEX format
   *
   * @param plaintext
   *
   * @return the crypted String as HEX format
   *
   * @throws Exception
   */
  public String cryptToHex(byte[] plaintext) throws Exception {
    byte[] result = crypt(plaintext);
    return encodeHex(result);
  }

  /**
   * @param bytes
   *
   * @return The encoded array of bytes in HEX
   */
  public String encodeHex(byte[] bytes) {
    return FilesystemBasedDigest.getHex(bytes);
  }

  /**
   * Returns a cipher for decryption associated with the key
   *
   * @return the cipher for decryption or null if it fails in case Encryption
   *     method or key is incorrect
   */
  public Cipher toDecrypt() {
    Cipher cipher;
    try {
      cipher = Cipher.getInstance(getAlgorithm());
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
    } catch (Exception e) {
      logger.warn("Uncrypt Error", e);
      return null;
    }
    return cipher;
  }

  /**
   * Decrypt an array of bytes and returns the uncrypted String
   *
   * @param ciphertext
   *
   * @return the uncrypted array of bytes
   *
   * @throws Exception
   */
  public String decryptInString(byte[] ciphertext) throws Exception {
    return new String(decrypt(ciphertext), WaarpStringUtils.UTF8);
  }

  /**
   * Decrypt an array of bytes and returns the uncrypted array of bytes
   *
   * @param ciphertext
   *
   * @return the uncrypted array of bytes
   *
   * @throws Exception
   */
  public byte[] decrypt(byte[] ciphertext) throws Exception {
    if (!keyReady()) {
      throw new CryptoException("Key not Ready");
    }
    try {
      Cipher cipher = Cipher.getInstance(getAlgorithm());
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      return cipher.doFinal(ciphertext);
    } catch (Exception e) {
      logger.warn("Decrypt Error", e);
      throw e;
    }
  }

  /**
   * Decrypt an array of bytes as HEX format representing a crypted array of
   * bytes and returns the uncrypted array of
   * bytes
   *
   * @param ciphertext
   *
   * @return the uncrypted array of bytes
   *
   * @throws Exception
   */
  public byte[] decryptHexInBytes(byte[] ciphertext) throws Exception {
    byte[] arrayBytes =
        decodeHex(new String(ciphertext, WaarpStringUtils.UTF8));
    return decrypt(arrayBytes);
  }

  /**
   * @param encoded
   *
   * @return the array of bytes from encoded String (HEX)
   */
  public byte[] decodeHex(String encoded) {
    return FilesystemBasedDigest.getFromHex(encoded);
  }

  /**
   * Decrypt a String as HEX format representing a crypted array of bytes and
   * returns the uncrypted String
   *
   * @param ciphertext
   *
   * @return the uncrypted String
   *
   * @throws Exception
   */
  public String decryptHexInString(String ciphertext) throws Exception {
    return new String(decryptHexInBytes(ciphertext), WaarpStringUtils.UTF8);
  }

  /**
   * Decrypt a String as HEX format representing a crypted array of bytes and
   * returns the uncrypted array of bytes
   *
   * @param ciphertext
   *
   * @return the uncrypted array of bytes
   *
   * @throws Exception
   */
  public byte[] decryptHexInBytes(String ciphertext) throws Exception {
    byte[] arrayBytes = decodeHex(ciphertext);
    return decrypt(arrayBytes);
  }

  /**
   * Decode from a file containing a HEX crypted string
   *
   * @param file
   *
   * @return the decoded uncrypted content of the file
   *
   * @throws Exception
   */
  public byte[] decryptHexFile(File file) throws Exception {
    if (file.length() > Integer.MAX_VALUE) {
      throw new IOException(
          "File too big to be decoded into an array of bytes");
    }
    byte[] byteKeys = new byte[(int) file.length()];
    FileInputStream inputStream = null;
    DataInputStream dis = null;
    try {
      inputStream = new FileInputStream(file);
      dis = new DataInputStream(inputStream);
      dis.readFully(byteKeys);
      dis.close();
      String skey = new String(byteKeys, WaarpStringUtils.UTF8);
      // decrypt it
      byteKeys = decryptHexInBytes(skey);
      return byteKeys;
    } catch (IOException e) {
      try {
        if (dis != null) {
          dis.close();
        } else if (inputStream != null) {
          inputStream.close();
        }
      } catch (IOException e1) {
      }
      throw e;
    }
  }
}
