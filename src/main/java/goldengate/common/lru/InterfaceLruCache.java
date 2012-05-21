/**
 * This file is part of GoldenGate Project (named also GoldenGate or GG).
 * 
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of
 * individual contributors.
 * 
 * All GoldenGate Project is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * GoldenGate is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * GoldenGate . If not, see <http://www.gnu.org/licenses/>.
 */
package goldengate.common.lru;

import java.util.concurrent.Callable;

/**
 * LRU cache interface.
 * 
 * @author Frederic Bregier
 * @author Damian Momot
 * 
 */
public interface InterfaceLruCache<K, V> {
    /**
     * Removes all entries from cache
     */
    public void clear();

    /**
     * Removes all oldest entries from cache (ttl based)
     */
    public void forceClearOldest();

    /**
     * Checks whether cache contains valid entry for key
     * 
     * @param key
     * @return true if cache contains key and entry is valid
     */
    public boolean contains(K key);

    /**
     * Returns value cached with key.
     * 
     * @param key
     * @return value or null if key doesn't exist or entry is not valid
     */
    public V get(K key);

    /**
     * Tries to get element from cache. If get fails callback is used to create
     * element and returned value is stored in cache.
     * 
     * Default TTL is used
     * 
     * @param key
     * @param callback
     * @return
     * @throws Exception
     *             if callback throws exception
     */
    public V get(K key, Callable<V> callback) throws Exception;

    /**
     * Tries to get element from cache. If get fails callback is used to create
     * element and returned value is stored in cache
     * 
     * @param key
     * @param callback
     * @param ttl
     *            time to live in milliseconds
     * @return
     * @throws Exception
     *             if callback throws exception
     */
    public V get(K key, Callable<V> callback, long ttl) throws Exception;

    /**
     * Returns cache capacity
     * 
     * @return capacity of cache
     */
    public int getCapacity();

    /**
     * Returns number of entries stored in cache (including invalid ones)
     * 
     * @return number of entries
     */
    public int getSize();

    /**
     * Returns cache TTL
     * 
     * @return ttl in milliseconds
     */
    public long getTtl();

    /**
     * Checks whether cache is empty.
     * 
     * If any entry exists (including invalid one) this method will return true
     * 
     * @return true if no entries are stored in cache
     */
    public boolean isEmpty();

    /**
     * Puts value under key into cache. Default TTL is used
     * 
     * @param key
     * @param value
     */
    public void put(K key, V value);

    /**
     * Puts value under key into cache with desired TTL
     * 
     * @param key
     * @param value
     * @param ttl
     *            time to live in milliseconds
     */
    public void put(K key, V value, long ttl);

    /**
     * Removes entry from cache (if exists)
     * 
     * @param key
     */
    public void remove(K key);
}
