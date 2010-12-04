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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

/**
 * XmlHash Hashtable for XmlValue utility
 * 
 * @author Frederic Bregier
 * 
 */
public class XmlHash {
    private Hashtable<String, XmlValue> hashtable;

    public XmlHash(XmlValue[] values) {
        hashtable = new Hashtable<String, XmlValue>();
        for (XmlValue xmlValue: values) {
            hashtable.put(xmlValue.getName(), xmlValue);
        }
    }

    public XmlValue get(String name) {
        return hashtable.get(name);
    }

    public XmlValue put(XmlValue value) {
        return hashtable.put(value.getName(), value);
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
