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
package org.waarp.common.state.example;

import org.waarp.common.state.Transition;

import java.util.EnumSet;

/**
 * Example of EnumState enum class
 *
 * @author Frederic Bregier
 */
public enum ExampleEnumState {
    RUNNING, PAUSED, CONFIGURING, RESET, ENDED;

    public static enum ExampleTransition {
        tRUNNING(RUNNING, EnumSet.of(PAUSED, ENDED)),
        tPAUSED(PAUSED, EnumSet.of(RUNNING, RESET, CONFIGURING)),
        tENDED(ENDED, EnumSet.of(RESET)),
        tCONFIGURING(CONFIGURING, EnumSet.of(PAUSED)),
        tRESET(RESET, EnumSet.of(PAUSED, RESET));

        public Transition<ExampleEnumState> elt;

        private ExampleTransition(ExampleEnumState state, EnumSet<ExampleEnumState> set) {
            this.elt = new Transition<ExampleEnumState>(state, set);
        }
    }

}
