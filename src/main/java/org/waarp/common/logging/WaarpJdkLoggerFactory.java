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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger factory which creates a
 * <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/logging/">java.util.logging</a>
 * logger.
 */
public class WaarpJdkLoggerFactory extends WaarpLoggerFactory {

    /**
     * @param level
     */
    public WaarpJdkLoggerFactory(final WaarpLogLevel level) {
        super(level);
        seLevelSpecific(currentLevel);
    }

    @Override
    public WaarpLogger newInstance(final String name) {
        return new WaarpJdkLogger(Logger.getLogger(name));
    }

    @Override
    protected void seLevelSpecific(final WaarpLogLevel level) {
        final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        switch (level) {
        case TRACE:
            logger.setLevel(Level.FINEST);
            break;
        case DEBUG:
            logger.setLevel(Level.FINE);
            break;
        case INFO:
            logger.setLevel(Level.INFO);
            break;
        case WARN:
            logger.setLevel(Level.WARNING);
            break;
        case ERROR:
            logger.setLevel(Level.SEVERE);
            break;
        default:
            logger.setLevel(Level.WARNING);
            break;
        }
    }

    @Override
    protected WaarpLogLevel getLevelSpecific() {
        final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        if (logger.isLoggable(Level.FINEST)) {
            return WaarpLogLevel.TRACE;
        } else if (logger.isLoggable(Level.FINE)) {
            return WaarpLogLevel.DEBUG;
        } else if (logger.isLoggable(Level.INFO)) {
            return WaarpLogLevel.INFO;
        } else if (logger.isLoggable(Level.WARNING)) {
            return WaarpLogLevel.WARN;
        } else if (logger.isLoggable(Level.SEVERE)) {
            return WaarpLogLevel.ERROR;
        }
        return null;
    }
}
