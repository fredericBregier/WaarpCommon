package org.waarp.common.state;

import static org.junit.Assert.*;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.waarp.common.exception.IllegalFiniteStateException;
import org.waarp.common.state.MachineStateTest.ExampleEnumState.ExampleTransition;

public class MachineStateTest {
    public static enum ExampleEnumState {
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

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testMachineStateEnumStateConcurrentHashMapOfEnumStateEnumSetOfQ() {
        // First create a HashMap and fill it directly
        ConcurrentHashMap<ExampleEnumState, EnumSet<ExampleEnumState>> stateMap =
                new ConcurrentHashMap<ExampleEnumState, EnumSet<ExampleEnumState>>();
        stateMap.put(ExampleTransition.tRUNNING.elt.state,
                (EnumSet<ExampleEnumState>) ExampleTransition.tRUNNING.elt.set);
        // Second create the MachineState with the right Map
        MachineState<ExampleEnumState> machineState1 =
                new MachineState(ExampleEnumState.PAUSED, stateMap);
        // Third, if not already done, fill the Map with the transitions
        for (ExampleTransition trans : ExampleTransition.values()) {
            machineState1.addNewAssociation(trans.elt);
        }
        System.out.println("Machine1 states...");
        assertTrue(changeState(machineState1, ExampleEnumState.CONFIGURING));
        assertTrue(changeState(machineState1, ExampleEnumState.PAUSED));
        assertTrue(changeState(machineState1, ExampleEnumState.RUNNING));
        assertTrue(changeState(machineState1, ExampleEnumState.ENDED));
        assertFalse(changeState(machineState1, ExampleEnumState.PAUSED));
        assertTrue(changeState(machineState1, ExampleEnumState.RESET));
        assertTrue(changeState(machineState1, ExampleEnumState.PAUSED));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testMachineStateEnumState() {
        // Or First create the MachineSate but empty
        MachineState<ExampleEnumState> machineState2 =
                new MachineState(ExampleEnumState.PAUSED);
        // Second fill the associations with transitions since none exist yet
        for (ExampleTransition trans : ExampleTransition.values()) {
            machineState2.addNewAssociation(trans.elt);
        }
        System.out.println("Machine2 states...");
        assertTrue(changeState(machineState2, ExampleEnumState.CONFIGURING));
        assertTrue(changeState(machineState2, ExampleEnumState.PAUSED));
        assertTrue(changeState(machineState2, ExampleEnumState.RUNNING));
        assertTrue(changeState(machineState2, ExampleEnumState.ENDED));
        assertFalse(changeState(machineState2, ExampleEnumState.PAUSED));
        assertTrue(changeState(machineState2, ExampleEnumState.RESET));
        assertTrue(changeState(machineState2, ExampleEnumState.PAUSED));
    }

    static private boolean changeState(MachineState<ExampleEnumState> mach,
            ExampleEnumState desired) {
        try {
            printState(mach);
            mach.setCurrent(desired);
            printState(mach);
            return true;
        } catch (IllegalFiniteStateException e) {
            printWrongState(mach, desired);
            return false;
        }
    }

    static private final void printState(MachineState<ExampleEnumState> mach) {
        System.out.println("State is " + mach.getCurrent());
    }

    static private final void printWrongState(MachineState<ExampleEnumState> mach,
            ExampleEnumState desired) {
        System.out.println("Cannot go from State " + mach.getCurrent() + " to State " + desired);
    }

}
