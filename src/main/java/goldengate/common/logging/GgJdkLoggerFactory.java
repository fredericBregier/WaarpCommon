/**
   This file is part of GoldenGate Project (named also GoldenGate or GG).

   Copyright 2009, Frederic Bregier, and individual contributors by the @author
   tags. See the COPYRIGHT.txt in the distribution for a full listing of
   individual contributors.

   All GoldenGate Project is free software: you can redistribute it and/or 
   modify it under the terms of the GNU General Public License as published 
   by the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   GoldenGate is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with GoldenGate .  If not, see <http://www.gnu.org/licenses/>.
 */
package goldengate.common.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.JdkLoggerFactory;

/**
 * Logger factory which creates a <a href=
 * "http://java.sun.com/javase/6/docs/technotes/guides/logging/index.html"
 * >java.util.logging</a> logger.
 *
 * Based on The Netty Project (netty-dev@lists.jboss.org)
 *
 * @author Trustin Lee (tlee@redhat.com)
 * @author Frederic Bregier
 */
public class GgJdkLoggerFactory extends JdkLoggerFactory {
    /**
     *
     * @param level
     */
    public GgJdkLoggerFactory(Level level) {
        super();

        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        if (level == null) {
            logger.info("Default level: "+logger.getLevel());
        } else {
            logger.setLevel(level);
        }
    }

    @Override
    public InternalLogger newInstance(String name) {
        final java.util.logging.Logger logger = java.util.logging.Logger
                .getLogger(name);
        return new GgJdkLogger(logger, name);
    }
}
