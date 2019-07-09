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
package org.waarp.common.xml;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

/**
 * XmlHash Hashtable for XmlValue utility. Hash all values (including subXml) but only root for
 * Multiple
 *
 * @author Frederic Bregier
 *
 */
public class XmlHash {
    private final Hashtable<String, XmlValue> hashtable;

    public XmlHash(XmlValue[] values) {
        hashtable = new Hashtable<String, XmlValue>();
        for (XmlValue xmlValue : values) {
            if (xmlValue.isMultiple()) {
                hashtable.put(xmlValue.getName(), xmlValue);
            } else if (xmlValue.isSubXml()) {
                this.put(xmlValue);
            } else {
                hashtable.put(xmlValue.getName(), xmlValue);
            }
        }
    }

    public XmlHash(XmlValue value) {
        hashtable = new Hashtable<String, XmlValue>();
        if (value == null) {
            return;
        }
        if (value.isMultiple()) {
            hashtable.put(value.getName(), value);
        } else if (value.isSubXml()) {
            this.put(value);
        } else {
            hashtable.put(value.getName(), value);
        }
    }

    public XmlValue get(String name) {
        return hashtable.get(name);
    }

    public XmlValue put(XmlValue value) {
        if (value.isMultiple()) {
            return hashtable.put(value.getName(), value);
        } else if (value.isSubXml()) {
            XmlValue ret = hashtable.put(value.getName(), value);
            if (!value.isEmpty()) {
                for (XmlValue subvalue : value.getSubXml()) {
                    if (subvalue != null) {
                        this.put(subvalue);
                    }
                }
            }
            return ret;
        } else {
            return hashtable.put(value.getName(), value);
        }
    }

    public int size() {
        return hashtable.size();
    }

    public boolean isEmpty() {
        return hashtable.isEmpty();
    }

    public Enumeration<String> keys() {
        return hashtable.keys();
    }

    public Enumeration<XmlValue> elements() {
        return hashtable.elements();
    }

    public boolean contains(XmlValue value) {
        return hashtable.contains(value);
    }

    public boolean containsValue(XmlValue value) {
        return hashtable.containsValue(value);
    }

    public boolean containsKey(String key) {
        return hashtable.containsKey(key);
    }

    public XmlValue remove(String key) {
        return hashtable.remove(key);
    }

    public void clear() {
        hashtable.clear();
    }

    public Set<String> keySet() {
        return hashtable.keySet();
    }

}
