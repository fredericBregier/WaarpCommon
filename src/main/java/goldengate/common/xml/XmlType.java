/**
 * Copyright 2009, Frederic Bregier, and individual contributors
 * by the @author tags. See the COPYRIGHT.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package goldengate.common.xml;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Type of Classes supported in Enum type
 * 
 * @author Frederic Bregier
 * 
 */
public enum XmlType {
    BOOLEAN(Boolean.TYPE), INTEGER(Integer.TYPE), FLOAT(Float.TYPE), CHARACTER(
            Character.TYPE), BYTE(Byte.TYPE), LONG(Long.TYPE), DOUBLE(
            Double.TYPE), SHORT(Short.TYPE), SQLDATE(Date.class), TIMESTAMP(
            Timestamp.class), STRING(String.class), XVAL(XmlValue.class);

    public Class<?> classType;

    /**
     * @param classType
     */
    private XmlType(Class<?> classType) {
        this.classType = classType;
    }

    /**
     * 
     * @return the associated Native Java class
     */
    public Class<?> getClassType() {
        return classType;
    }

    /**
     * 
     * @param value
     * @return True if the Object is natively compatible with the internal Type
     */
    public boolean isNativelyCompatible(Object value) {
        Class<?> type = value.getClass();
        switch (this) {
            case BOOLEAN:
                return (type.equals(Boolean.TYPE) || Boolean.class
                        .isAssignableFrom(type));
            case INTEGER:
                return (type.equals(Integer.TYPE) || Integer.class
                        .isAssignableFrom(type));
            case FLOAT:
                return (type.equals(Float.TYPE) || Float.class
                        .isAssignableFrom(type));
            case CHARACTER:
                return (type.equals(Character.TYPE) || Character.class
                        .isAssignableFrom(type));
            case BYTE:
                return (type.equals(Byte.TYPE) || Byte.class
                        .isAssignableFrom(type));
            case LONG:
                return (type.equals(Long.TYPE) || Long.class
                        .isAssignableFrom(type));
            case DOUBLE:
                return (type.equals(Double.TYPE) || Double.class
                        .isAssignableFrom(type));
            case SHORT:
                return (type.equals(Short.TYPE) || Short.class
                        .isAssignableFrom(type));
            case SQLDATE:
                return (java.sql.Date.class.isAssignableFrom(type));
            case TIMESTAMP:
                return (Timestamp.class.isAssignableFrom(type));
            case STRING:
                return (String.class.isAssignableFrom(type));
            case XVAL:
                if (type.isArray() &&
                        type.getName().contains(XmlValue.class.getName())) {
                    return true;
                }
                return false;
        }
        return false;
    }

    public boolean isBoolean() {
        return this == BOOLEAN;
    }

    public boolean isInteger() {
        return this == INTEGER;
    }

    public boolean isFloat() {
        return this == FLOAT;
    }

    public boolean isCharacter() {
        return this == CHARACTER;
    }

    public boolean isByte() {
        return this == BYTE;
    }

    public boolean isLong() {
        return this == LONG;
    }

    public boolean isDouble() {
        return this == DOUBLE;
    }

    public boolean isShort() {
        return this == SHORT;
    }

    public boolean isDate() {
        return this == SQLDATE;
    }

    public boolean isTimestamp() {
        return this == TIMESTAMP;
    }

    public boolean isString() {
        return this == STRING;
    }

    public boolean isXmlValue() {
        return this == XVAL;
    }
}