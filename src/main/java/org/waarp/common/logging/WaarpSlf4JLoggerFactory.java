/**
 * This file is part of VITAM Project.
 * <p>
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author tags. See the COPYRIGHT.txt in the
 * distribution for a full listing of individual contributors.
 * <p>
 * All VITAM Project is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * VITAM is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with VITAM . If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * logger factory using SLF4J from LOGBACK
 *
 * @author Frederic Bregier
 */
public class WaarpSlf4JLoggerFactory extends WaarpLoggerFactory {
    static final String ROOT = Logger.ROOT_LOGGER_NAME;

    // Old versions: "root"; // LoggerContext.ROOT_NAME; //

    /**
     * @param level
     */
    public WaarpSlf4JLoggerFactory(final WaarpLogLevel level) {
        super(level);
        seLevelSpecific(currentLevel);
    }

    WaarpSlf4JLoggerFactory(final boolean failIfNOP) {
        super(null);
        assert failIfNOP; // Should be always called with true.

        // SFL4J writes it error messages to System.err. Capture them so that the user does not see such a message on
        // the console during automatic detection.
        final StringBuffer buf = new StringBuffer();
        final PrintStream err = System.err;
        try {
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(final int b) {
                    buf.append((char) b);
                }
            }, true, "US-ASCII"));
        } catch (final UnsupportedEncodingException e) {
            throw new Error(e);
        }

        try {
            if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
                throw new NoClassDefFoundError(buf.toString());
            } else {
                err.print(buf.toString());
                err.flush();
            }
        } finally {
            System.setErr(err);
            seLevelSpecific(currentLevel);
        }
    }

    @Override
    protected void seLevelSpecific(final WaarpLogLevel level) {
        final Logger logger = (Logger) LoggerFactory.getLogger(ROOT);
        switch (level) {
        case TRACE:
            logger.setLevel(Level.TRACE);
            break;
        case DEBUG:
            logger.setLevel(Level.DEBUG);
            break;
        case INFO:
            logger.setLevel(Level.INFO);
            break;
        case WARN:
            logger.setLevel(Level.WARN);
            break;
        case ERROR:
            logger.setLevel(Level.ERROR);
            break;
        default:
            logger.setLevel(Level.WARN);
            break;
        }
    }

    @Override
    public WaarpLogger newInstance(final String name) {
        final Logger logger = (Logger) LoggerFactory.getLogger(name);
        return new WaarpSlf4JLogger(logger);
    }

    @Override
    protected WaarpLogLevel getLevelSpecific() {
        final Logger logger = (Logger) LoggerFactory.getLogger(ROOT);
        if (logger.isTraceEnabled()) {
            return WaarpLogLevel.TRACE;
        } else if (logger.isDebugEnabled()) {
            return WaarpLogLevel.DEBUG;
        } else if (logger.isInfoEnabled()) {
            return WaarpLogLevel.INFO;
        } else if (logger.isWarnEnabled()) {
            return WaarpLogLevel.WARN;
        } else if (logger.isErrorEnabled()) {
            return WaarpLogLevel.ERROR;
        }
        return null;
    }
}
