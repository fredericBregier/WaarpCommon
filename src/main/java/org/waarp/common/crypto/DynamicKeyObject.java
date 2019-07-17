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

/**
 * This class handles methods to encrypt and unencrypt messages with any
 * available algorithms in the JVM.<br>
 * <b>AES is the best compromise in term of security and efficiency.</b> <br>
 * Usage:<br>
 * <ul>
 * <li>Create a Key object: DynamicKey key = new DynamicKey(size,algo,instance,extension);<br>
 * As DynamicKey(56, "DES", "DES/ECB/PKCS5Padding", "des") for DES<br> or
 * through Enum class INSTANCES (minimal
 * supported size) or INSTANCESMAX (maximal supported size) as
 * DynamicKey(INSTANCESMAX.AES,"aes")</li>
 * <li>Create a key:
 * <ul>
 * <li>Generate: key.generateKey();<br>
 * The method key.getSecretKeyInBytes() allow getting the key in Bytes.</li>
 * <li>From an external source: key.setSecretKey(arrayOfBytes);</li>
 * </ul>
 * </li>
 * <li>To encrypt a String in a Base64 format: String myStringCrypt =
 * key.cryptToString(myString);</li>
 * <li>To unencrypt one string from Base64 format to the original String:
 * String
 * myStringDecrypt =
 * key.decryptStringInString(myStringCrypte);</li>
 * </ul>
 *
 * @author frederic bregier
 */
public class DynamicKeyObject extends KeyObject {

  /**
   * This value could be between 32 and 128 due to license limitation.
   */
  private final int KEY_SIZE;
  /**
   * Short name for the algorithm
   */
  private final String ALGO;
  /**
   * Could be the shortname again (default implementation in JVM) or the full
   * name as DES/ECB/PKCS5Padding
   */
  private final String INSTANCE;
  /**
   * The extension for the file to use when saving the key (note that an extra
   * file as extension.inf will be also
   * saved for the extra information)
   */
  private final String EXTENSION;

  /**
   * @param kEYSIZE example DES: 56
   * @param aLGO example DES: DES
   * @param iNSTANCE example DES: DES/ECB/PKCS5Padding
   * @param eXTENSION example DES: des
   */
  public DynamicKeyObject(int kEYSIZE, String aLGO, String iNSTANCE,
                          String eXTENSION) {
    super();
    KEY_SIZE = kEYSIZE;
    ALGO = aLGO;
    INSTANCE = iNSTANCE;
    EXTENSION = eXTENSION;
  }

  /**
   * @param instance the minimal default instance
   * @param eXTENSION to use for files
   */
  public DynamicKeyObject(INSTANCES instance,
                          String eXTENSION) {
    super();
    KEY_SIZE = instance.size;
    ALGO = instance.name();
    INSTANCE = instance.name();
    EXTENSION = eXTENSION;
  }

  /**
   * @param instance the maximal default instance
   * @param eXTENSION to use for files
   */
  public DynamicKeyObject(INSTANCESMAX instance,
                          String eXTENSION) {
    super();
    KEY_SIZE = instance.size;
    ALGO = instance.name();
    INSTANCE = instance.name();
    EXTENSION = eXTENSION;
  }

  @Override
  public String getAlgorithm() {
    return ALGO;
  }

  @Override
  public String getInstance() {
    return INSTANCE;
  }

  @Override
  public int getKeySize() {
    return KEY_SIZE;
  }

  @Override
  public String getFileExtension() {
    return EXTENSION;
  }

  /**
   * Minimal key size
   *
   * @author Frederic Bregier
   */
  public static enum INSTANCES {
    AES(128), // Min 128
    ARCFOUR(56), // No Max
    Blowfish(56), // No Max
    DES(56), // Must be 56 except if Strong Policy is used
    DESede(112), // 112 or 168 triple DES
    RC2(56),
    RC4(56);

    int size;

    private INSTANCES(int size) {
      this.size = size;
    }
  }

  /**
   * Recommended key size when normal JVM installed (no extension on encrypt
   * support)
   *
   * @author Frederic Bregier
   */
  public static enum INSTANCESMAX {
    AES(128), // Min 128
    ARCFOUR(128), // No Max
    Blowfish(128), // No Max
    DES(56), // Must be 56 except if Strong Policy is used
    DESede(168), // 112 or 168 triple DES
    RC2(128),
    RC4(128);

    int size;

    private INSTANCESMAX(int size) {
      this.size = size;
    }
  }
}
