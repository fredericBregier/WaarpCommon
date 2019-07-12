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
package org.waarp.common.file;

/**
 * Interface for Option support (mainly inspired for MLSx FTP command)
 *
 * @author Frederic Bregier
 */
public interface OptsMLSxInterface {
    /**
     * @return the optsCharset
     */
    public byte getOptsCharset();

    /**
     * @param optsCharset the optsCharset to set
     */
    public void setOptsCharset(byte optsCharset);

    /**
     * @return the optsCreate
     */
    public byte getOptsCreate();

    /**
     * @param optsCreate the optsCreate to set
     */
    public void setOptsCreate(byte optsCreate);

    /**
     * @return the optsLang
     */
    public byte getOptsLang();

    /**
     * @param optsLang the optsLang to set
     */
    public void setOptsLang(byte optsLang);

    /**
     * @return the optsMediaType
     */
    public byte getOptsMediaType();

    /**
     * @param optsMediaType the optsMediaType to set
     */
    public void setOptsMediaType(byte optsMediaType);

    /**
     * @return the optsModify
     */
    public byte getOptsModify();

    /**
     * @param optsModify the optsModify to set
     */
    public void setOptsModify(byte optsModify);

    /**
     * @return the optsPerm
     */
    public byte getOptsPerm();

    /**
     * @param optsPerm the optsPerm to set
     */
    public void setOptsPerm(byte optsPerm);

    /**
     * @return the optsSize
     */
    public byte getOptsSize();

    /**
     * @param optsSize the optsSize to set
     */
    public void setOptsSize(byte optsSize);

    /**
     * @return the optsType
     */
    public byte getOptsType();

    /**
     * @param optsType the optsType to set
     */
    public void setOptsType(byte optsType);

    /**
     * @return the optsUnique
     */
    public byte getOptsUnique();

    /**
     * @param optsUnique the optsUnique to set
     */
    public void setOptsUnique(byte optsUnique);

    /**
     * @return the String associated to the feature for MLSx
     */
    public String getFeat();
}
