/*
 * Copyright (c) 2019, to individual contributors by the @author tags.
 * See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 *
 * This file is part of Waarp Project.
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

package org.waarp.common.database.properties;

public abstract class DbProperties {
    
    /**
     * @return the driver class name associated with the DbModel
     */
    abstract public String getDriverName();
    
    /**
     * @return the validation query associated with the DbModel
     */
    abstract public String getValidationQuery();
}
