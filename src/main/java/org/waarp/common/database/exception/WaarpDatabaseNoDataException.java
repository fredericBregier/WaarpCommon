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
package org.waarp.common.database.exception;

/**
 * Database no data exception
 *
 * @author Frederic Bregier
 *
 */
public class WaarpDatabaseNoDataException extends WaarpDatabaseException {

    /**
     *
     */
    private static final long serialVersionUID = -1148593385347608219L;

    /**
     *
     */
    public WaarpDatabaseNoDataException() {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public WaarpDatabaseNoDataException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public WaarpDatabaseNoDataException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public WaarpDatabaseNoDataException(Throwable arg0) {
        super(arg0);
    }

}
