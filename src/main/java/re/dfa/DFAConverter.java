package re.dfa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import re.fa.SpecialTransitions;
import re.fa.StateTable;
import re.logger.Logger;

public class DFAConverter {
    public static StateTable convertToDFA(StateTable table) {
        StateTable DFATable;
        // table = removeDeadEnds(table);
        // table = removeUnreachable(table);
        // Logger.log("States removed. Table properties:");
        // Logger.log(Logger.extractProperties(table));
        DFATable = eliminateNonDeterminism(table);
        Logger.log("Non-determinism eliminated. Table properties:");
        Logger.log(Logger.extractProperties(table));
        DFATable = removeDeadEnds(DFATable);
        DFATable = removeUnreachable(DFATable);
        DFATable = mergeEquivalent(DFATable);
        DFATable = completeTable(DFATable);
        // DFATable.eraseDeleted();
        return DFATable;
    }

    public static StateTable completeTable(StateTable table) {
        Logger.log("Completing table...");
        StateTable DFATable = new StateTable(table);
        DFATable.addFreeTransition(DFATable.getFinalState(), DFATable.getFinalState());
        DFATable = makeDeadState(DFATable);
        DFATable.addTransitionLiteral(SpecialTransitions.otherTransition);
        for (int state : DFATable.getRelevantStates()) {
            for (int transLit = 0; transLit < DFATable.stateTable.get(state).size(); transLit++) {
                if (DFATable.stateTable.get(state).get(transLit).size() == 0) {
                    DFATable.addTransition(DFATable.transitionLiterals.get(transLit), state, DFATable.getDeadState());
                }
            }
        }

        return DFATable;
    }

    public static StateTable makeDeadState(StateTable table) {
        StateTable DFATable = new StateTable(table);
        DFATable.setDeadState(DFATable.addState());

        return DFATable;

    }

    public static StateTable mergeEquivalent(StateTable table) {
        Logger.log("Merging equivalent...");
        StateTable DFATable = new StateTable(table);
        List<Integer> equivalentStates = findFirstEquivalent(DFATable);
        while (equivalentStates.size() != 0) {
            DFATable.mergeStates(equivalentStates.get(0), equivalentStates.get(1));
            // DFATable = removeDeadEnds(DFATable);
            // DFATable = removeUnobtainable(DFATable);
            equivalentStates.clear();
            equivalentStates = findFirstEquivalent(DFATable);
        }

        return DFATable;
    }

    public static List<Integer> findFirstEquivalent(StateTable table) {
        List<Integer> newEquivalentStates = new ArrayList<Integer>();
        for (int state1 : table.getRelevantStates()) {
            for (int state2 : table.getRelevantStates()) {
                if ((state1 != state2) && (!table.isFinal(state1) || !table.isFinal(state2))) {
                    List<Integer> differentTransitions = new ArrayList<Integer>();
                    List<Integer> state1Destinations = table.stateDestinations(state1);
                    List<Integer> state2Destinations = table.stateDestinations(state2);
                    Collections.sort(state1Destinations);
                    Collections.sort(state2Destinations);
                    if (state1Destinations.equals(state2Destinations)) {
                        newEquivalentStates.add(state1);
                        newEquivalentStates.add(state2);
                        return newEquivalentStates;
                    }
                    for (int transLit = 0; transLit < table.stateTable.get(state1).size(); transLit++) {
                        Collections.sort(table.stateTable.get(state1).get(transLit));
                        Collections.sort(table.stateTable.get(state2).get(transLit));
                        List<Integer> state1Transitions = table.stateTable.get(state1).get(transLit);
                        List<Integer> state2Transitions = table.stateTable.get(state2).get(transLit);
                        if (!state1Transitions.equals(state2Transitions)) {
                            differentTransitions.add(transLit);
                        }
                    }
                    if (differentTransitions.size() == 1) {
                        int transLit = differentTransitions.get(0);
                        if (transitionsMutual(table, transLit, state1, state2)) {
                            table.stateTable.get(state2).get(transLit).clear();
                            table.stateTable.get(state1).get(transLit).clear();
                            table.stateTable.get(state1).get(transLit).add(state1);
                            newEquivalentStates.add(state1);
                            newEquivalentStates.add(state2);
                            return newEquivalentStates;
                        }
                    }
                }
            }
        }
        return newEquivalentStates;
    }

    public static boolean transitionsMutual(StateTable table, int transLit, int state1, int state2) {
        // check for exterior transitions
        for (int transition = 0; transition < table.stateTable.get(state1).get(transLit).size(); transition++) {
            if (table.getTransition(state1, transLit, transition) != state1
                    && table.getTransition(state1, transLit, transition) != state2) {
                return false;
            }
        }
        for (int transition = 0; transition < table.stateTable.get(state2).get(transLit).size(); transition++) {
            if (table.getTransition(state2, transLit, transition) != state1
                    && table.getTransition(state2, transLit, transition) != state2) {
                return false;
            }
        }
        // check for mutualness
        if ((table.stateTable.get(state1).get(transLit).contains(state1) /* both lead to themselves */
                && table.stateTable.get(state2).get(transLit).contains(state2))
                || (table.stateTable.get(state1).get(transLit).contains(state2) /* both lead to each other */
                        && table.stateTable.get(state2).get(transLit).contains(state1))
                || (table.stateTable.get(state1).get(transLit).contains(state1) /* first leads to itself */
                        && table.stateTable.get(state2).get(transLit).contains(state1)) /* and second leads to first */
                || (table.stateTable.get(state1).get(transLit).contains(state2) /* first leads to second */
                        && table.stateTable.get(state2).get(transLit) /* and second leads to itself */
                                .contains(state2))) {
            return true;
        }

        return false;
    }

    public static StateTable removeUnreachable(StateTable table) {
        Logger.log("Removing unreachable states...");
        List<Integer> unobtainable = new ArrayList<Integer>();
        StateTable DFATable = new StateTable(table);
        for (int state : table.getRelevantStates()) {
            if (!stateLeadsToState(table, table.getStartState(), new ArrayList<Integer>(), state)) {
                unobtainable.add(state);
            }
        }
        for (int unobtainableState = unobtainable.size() - 1; unobtainableState >= 0; unobtainableState--) {
            DFATable.removeTransitionsToState(unobtainable.get(unobtainableState));
            DFATable.removeState(unobtainable.get(unobtainableState));
        }
        return DFATable;
    }

    public static StateTable removeDeadEnds(StateTable table) {
        Logger.log("Removing dead-end states...");
        List<Integer> deadEnds = new ArrayList<Integer>();
        StateTable DFATable = new StateTable(table);
        for (int state : table.getRelevantStates()) {
            if (table.isDeleted(state)) {
                continue;
            }
            if (!stateLeadsToFinal(table, state, new ArrayList<Integer>())) {
                deadEnds.add(state);
            }
        }
        for (int deadEnd = deadEnds.size() - 1; deadEnd >= 0; deadEnd--) {
            DFATable.removeTransitionsToState(deadEnds.get(deadEnd));
            DFATable.removeState(deadEnds.get(deadEnd));
        }
        return DFATable;
    }

    private static boolean stateLeadsToFinal(StateTable table, int state, List<Integer> marked) {
        if (table.isFinal(state)) {
            return true;
        }
        for (int transLit = 0; transLit < table.stateTable.get(state).size(); transLit++) {
            for (int transition : table.stateTable.get(state).get(transLit)) {
                if (!marked.contains(transition)) {
                    marked.add(transition);
                    if (stateLeadsToFinal(table, transition, marked)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean stateLeadsToState(StateTable table, int state, List<Integer> marked, int destinationState) {
        if (state == destinationState) {
            return true;
        }
        for (int transLit = 0; transLit < table.stateTable.get(state).size(); transLit++) {
            for (int transition : table.stateTable.get(state).get(transLit)) {
                if (!marked.contains(transition)) {
                    marked.add(transition);
                    if (stateLeadsToState(table, transition, marked, destinationState)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static StateTable eliminateNonDeterminism(StateTable table) {
        Logger.log("Eliminating non-determinism...");
        // (。_。)
        StateTable DFATable = new StateTable();
        DFATable.removeState(1);
        DFATable.removeState(0);
        List<List<Integer>> handledStateLists = new ArrayList<List<Integer>>();
        List<Integer> startList = new ArrayList<Integer>();
        // step 1: add list consisting of startState to unhandledStateLists
        startList.add(table.getStartState());
        startList = buildEpsilonClosure(table, startList);
        eliminateNDCycle(table, handledStateLists, startList, DFATable);
        setFinalByHandled(table, DFATable, handledStateLists);
        return DFATable;

    }

    public static void setFinalByHandled(StateTable table, StateTable DFATable, List<List<Integer>> handledStateLists) {
        int finalState = table.getFinalState();
        for (List<Integer> states : handledStateLists) {
            for (int state : states) {
                if (state == finalState) {
                    DFATable.setFinalState(handledStateLists.indexOf(states));
                }
                if (table.metaFinalStates.contains(state)) {
                    DFATable.addMetaFinalState(handledStateLists.indexOf(states), table.getMetaFinalName(state));
                }
            }
        }
    }

    public static List<Integer> eliminateNDCycle(StateTable table, List<List<Integer>> handledStateLists,
            List<Integer> currentStates, StateTable DFATable) {
        List<String> currentStateLits = null;
        addAllUnique(handledStateLists, currentStates);
        currentStateLits = litsFromStates(table, currentStates);
        Logger.log("New state added.");
        DFATable.addState();
        for (String lit : currentStateLits) {
            List<Integer> newStates = destinationByLitFromStates(table, currentStateLits, currentStates, lit);
            newStates = buildEpsilonClosure(table, newStates);// complements currentStates
            if (!handledStateLists.contains(newStates)) {
                eliminateNDCycle(table, handledStateLists, newStates, DFATable);
            }
            int newStatesIndex = handledStateLists.indexOf(newStates);
            int thisStatesIndex = handledStateLists.indexOf(currentStates);
            DFATable.addTransition(lit, thisStatesIndex, newStatesIndex);
        }
        return currentStates;

    }

    public static List<Integer> destinationByLitFromStates(StateTable table, List<String> currentStateLits,
            List<Integer> currentStates, String lit) {
        // 2.4 make set of destination states from currentStates by currentStateLits
        List<Integer> newStates = new ArrayList<Integer>();
        int transLit = table.transitionLiterals.indexOf(lit);
        for (int state : currentStates) {
            for (int transition : table.stateTable.get(state).get(transLit)) {
                // exclude free non-final transitions
                if (!(lit == SpecialTransitions.freeTransition && !table.isFinal(transition))) {
                    newStates.add(transition);
                }
            }
        }
        return newStates;
    }

    public static <E> void addAllUnique(List<E> comprising, E element) {
        if (!comprising.contains(element)) {
            comprising.add(element);
        }
    }

    public static List<String> litsFromStates(StateTable table, List<Integer> currentStates) {
        // 2.3 make a set of literals from transitions from currentStates states
        List<String> currentStateLits = new ArrayList<String>();
        for (int state = 0; state < currentStates.size(); state++) {
            int currentState = currentStates.get(state);
            if (table.isDeleted(currentState)) {
                continue;
            }
            for (int transLit = 0; transLit < table.stateTable.get(currentState).size(); transLit++) {
                for (int transition = 0; transition < table.stateTable.get(currentState).get(transLit)
                        .size(); transition++) {
                    int currentTransition = table.stateTable.get(currentState).get(transLit).get(transition);
                    // if transition isn't free to non-final state
                    if (!(!table.isFinal(currentTransition)
                            && table.transitionLiterals.get(transLit) == SpecialTransitions.freeTransition)) {
                        // exclude duplicates
                        if (!currentStateLits.contains(table.transitionLiterals.get(transLit))) {
                            currentStateLits.add(table.transitionLiterals.get(transLit));
                        }
                    }
                }
            }
        }
        return currentStateLits;
    }

    public static List<Integer> buildEpsilonClosure(StateTable table, List<Integer> states) {
        // 2.1 build epsilon-closure of each of states in currentStates
        List<Integer> resultStates = new ArrayList<Integer>();
        for (Integer state : states) {
            List<Integer> closureStates = buildEpsilonClosure(table, state);
            for (Integer newState : closureStates) {
                if (!resultStates.contains(newState)) {
                    resultStates.add(newState);
                }
            }
        }
        return resultStates;
    }

    public static List<Integer> buildEpsilonClosure(StateTable table, int startState) {
        List<Integer> closureStates = new ArrayList<Integer>();
        closureStates.add(startState);
        int freeTransLitIndex = table.transitionLiterals.indexOf(SpecialTransitions.freeTransition);
        if (freeTransLitIndex != -1) {
            for (int freeTransition = 0; freeTransition < table.stateTable.get(startState).get(freeTransLitIndex)
                    .size(); freeTransition++) {
                List<Integer> childStates = buildEpsilonClosure(table,
                        table.getTransition(startState, freeTransLitIndex, freeTransition));
                for (Integer state : childStates) {
                    if (!closureStates.contains(state) && (!table.isFinal(state) && (!table.isDeleted(state)))) {
                        closureStates.add(state);
                    }
                }
            }
        }
        return closureStates;
    }
}