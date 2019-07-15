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
package org.waarp.common.lru;

import java.util.concurrent.Callable;

/**
 * Base class for concrete implementations
 *
 * @author Frederic Bregier
 * @author Damian Momot
 */
public abstract class AbstractLruCache<K, V>
    implements InterfaceLruCache<K, V> {
  private long ttl;

  /**
   * Constructs BaseLruCache
   *
   * @param ttl
   *
   * @throws IllegalArgumentException if ttl is not positive
   */
  protected AbstractLruCache(long ttl) {
    if (ttl <= 0) {
      throw new IllegalArgumentException("ttl must be positive");
    }

    this.ttl = ttl;
  }

  public boolean contains(K key) {
    // can't use contains because of expiration policy
    V value = get(key);

    return value != null;
  }

  public V get(K key) {
    return getValue(key);
  }

  /**
   * Tries to retrieve value by it's key. Automatically removes entry if it's
   * not valid (LruCacheEntry.getValue()
   * returns null)
   *
   * @param key
   *
   * @return Value
   */
  protected V getValue(K key) {
    V value = null;

    InterfaceLruCacheEntry<V> cacheEntry = getEntry(key);

    if (cacheEntry != null) {
      value = cacheEntry.getValue();

      // autoremove entry from cache if it's not valid
      if (value == null) {
        remove(key);
      }
    }

    return value;
  }

  /**
   * Returns LruCacheEntry mapped by key or null if it does not exist
   *
   * @param key
   *
   * @return LruCacheEntry<V>
   */
  abstract protected InterfaceLruCacheEntry<V> getEntry(K key);

  public V get(K key, Callable<V> callback) throws Exception {
    return get(key, callback, ttl);
  }

  public V get(K key, Callable<V> callback, long ttl) throws Exception {
    V value = get(key);

    // if element doesn't exist create it using callback
    if (value == null) {
      value = callback.call();
      put(key, value, ttl);
    }

    return value;
  }

  public void put(K key, V value, long ttl) {
    if (value != null) {
      putEntry(key, createEntry(value, ttl));
    }
  }

  /**
   * Puts entry into cache
   *
   * @param key
   * @param entry
   */
  abstract protected void putEntry(K key, InterfaceLruCacheEntry<V> entry);

  /**
   * Creates new LruCacheEntry<V>.
   * <p>
   * It can be used to change implementation of LruCacheEntry
   *
   * @param value
   * @param ttl
   *
   * @return LruCacheEntry<V>
   */
  protected InterfaceLruCacheEntry<V> createEntry(V value, long ttl) {
    return new StrongReferenceCacheEntry<V>(value, ttl);
  }

  public long getTtl() {
    return ttl;
  }

  public void setNewTtl(long ttl) {
    if (ttl <= 0) {
      throw new IllegalArgumentException("ttl must be positive");
    }
    this.ttl = ttl;
  }

  public void updateTtl(K key) {
    InterfaceLruCacheEntry<V> cacheEntry = getEntry(key);
    if (cacheEntry != null) {
      cacheEntry.resetTime(ttl);
    }
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public void put(K key, V value) {
    put(key, value, ttl);
  }
}
