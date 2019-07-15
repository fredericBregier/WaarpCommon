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

/*
 * Copyright 2012 The Netty Project
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.waarp.common.logging;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

/**
 * Creates an {@link WaarpLogger} or changes the default factory
 * implementation.
 * This factory allows you to choose what
 * logging framework VITAM should use. The default factory is {@link
 * WaarpSlf4JLoggerFactory}. If SLF4J is not
 * available, {@link WaarpSlf4JLoggerFactory} is used. If Log4J is not
 * available, {@link WaarpJdkLoggerFactory} is used.
 * You can change it to your preferred logging framework before other VITAM
 * classes are loaded:
 *
 * <pre>
 * {@link WaarpLoggerFactory}.setDefaultFactory(new {@link WaarpSlf4JLoggerFactory}());
 * </pre>
 * <p>
 * Please note that the new default factory is effective only for the classes
 * which were loaded after the default
 * factory is changed. Therefore, {@link #setDefaultFactory(WaarpLoggerFactory)}
 * should be called as early as possible
 * and shouldn't be called more than once.
 */
public abstract class WaarpLoggerFactory {
  protected static WaarpLogLevel currentLevel = null;
  private static volatile WaarpLoggerFactory defaultFactory;

  static {
    final String name = WaarpLoggerFactory.class.getName();
    WaarpLoggerFactory f;
    try {
      f = new WaarpSlf4JLoggerFactory(true);
      f.newInstance(name)
       .debug("Using Logback (SLF4J) as the default logging framework");
      defaultFactory = f;
    } catch (final Throwable t1) {
      f = new WaarpJdkLoggerFactory(null);
      f.newInstance(name)
       .debug("Using java.util.logging as the default logging framework", t1);
    }

    defaultFactory = f;
  }

  /**
   * @param level
   */
  public WaarpLoggerFactory(final WaarpLogLevel level) {
    setInternalLogLevel(level);
    if (currentLevel == null) {
      setInternalLogLevel(getLevelSpecific());
    }
  }

  protected static synchronized void setInternalLogLevel(
      final WaarpLogLevel level) {
    if (level != null) {
      currentLevel = level;
    }
  }

  /**
   * @return should return the current Level for the specific implementation
   */
  protected abstract WaarpLogLevel getLevelSpecific();

  /**
   * Creates a new logger instance with the name of the specified class.
   *
   * @param clazz
   *
   * @return the logger instance
   */
  public static WaarpLogger getInstance(final Class<?> clazz) {
    return getInstance(clazz.getName());
  }

  /**
   * Creates a new logger instance with the specified name.
   *
   * @param name
   *
   * @return the logger instance
   */
  public static WaarpLogger getInstance(final String name) {
    return getDefaultFactory().newInstance(name);
  }

  /**
   * Creates a new logger instance with the specified name.
   */
  protected abstract WaarpLogger newInstance(String name);

  /**
   * Returns the default factory. The initial default factory is {@link
   * WaarpJdkLoggerFactory}.
   *
   * @return the current default Factory
   */
  public static WaarpLoggerFactory getDefaultFactory() {
    return defaultFactory;
  }

  /**
   * Changes the default factory.
   *
   * @param defaultFactory
   */
  public static void setDefaultFactory(
      final WaarpLoggerFactory defaultFactory) {
    if (defaultFactory == null) {
      throw new NullPointerException("defaultFactory");
    }
    WaarpLoggerFactory.defaultFactory = defaultFactory;
    if (defaultFactory instanceof WaarpJdkLoggerFactory) {
      InternalLoggerFactory.setDefaultFactory(new JdkLoggerFactory());
    } else if (defaultFactory instanceof WaarpSlf4JLoggerFactory) {
      InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }
    defaultFactory.seLevelSpecific(defaultFactory.getLevelSpecific());
  }

  /**
   * Set the level for the specific implementation
   *
   * @param level
   */
  protected abstract void seLevelSpecific(WaarpLogLevel level);

  /**
   * Creates a new logger instance with the name of the specified class.
   *
   * @param clazz
   *
   * @return the logger instance
   */
  public static WaarpLogger getLogger(final Class<?> clazz) {
    return getInstance(clazz.getName());
  }

  /**
   * Creates a new logger instance with the specified name.
   *
   * @param name
   *
   * @return the logger instance
   */
  public static WaarpLogger getLogger(final String name) {
    return getDefaultFactory().newInstance(name);
  }

  public static WaarpLogLevel getLogLevel() {
    return currentLevel;
  }

  /**
   * @param level
   */
  public static void setLogLevel(final WaarpLogLevel level) {
    setInternalLogLevel(level);
    if (currentLevel != null) {
      getDefaultFactory().seLevelSpecific(currentLevel);
    }
  }
}
