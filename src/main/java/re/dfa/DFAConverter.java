package re.dfa;

import java.util.ArrayList;
import java.util.List;

import re.fa.SpecialTransitions;
import re.fa.StateTable;

public class DFAConverter {
    public static StateTable convertToDFA(StateTable table) {
        StateTable DFATable = eliminateNonDeterminism(table);
        DFATable = removeDeadEnds(DFATable);
        return DFATable;
    }

    public static StateTable removeUnobtainable(StateTable table) {
        List<Integer> unobtainable = new ArrayList<Integer>();
        StateTable DFATable = new StateTable(table);
        for (int state = 0; state < table.stateTable.size(); state++) {
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
        List<Integer> deadEnds = new ArrayList<Integer>();
        StateTable DFATable = new StateTable(table);
        for (int state = 0; state < table.stateTable.size(); state++) {
            if (!stateLeadsToState(table, state, new ArrayList<Integer>(), table.getFinalState())) {
                deadEnds.add(state);
            }
        }
        for (int deadEnd = deadEnds.size() - 1; deadEnd >= 0; deadEnd--) {
            DFATable.removeTransitionsToState(deadEnds.get(deadEnd));
            DFATable.removeState(deadEnds.get(deadEnd));
        }
        return DFATable;
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
            }
        }
    }

    public static List<Integer> eliminateNDCycle(StateTable table, List<List<Integer>> handledStateLists,
            List<Integer> currentStates, StateTable DFATable) {
        List<String> currentStateLits = null;
        addAllUnique(handledStateLists, currentStates);
        currentStateLits = litsFromStates(table, currentStates);
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
        int transLit = table.inputLits.indexOf(lit);
        for (int state : currentStates) {
            for (int transition : table.stateTable.get(state).get(transLit)) {
                // exclude free non-final transitions
                if (!(lit == SpecialTransitions.freeTransition && transition != table.getFinalState())) {
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
            for (int transLit = 0; transLit < table.stateTable.get(currentState).size(); transLit++) {
                for (int transition = 0; transition < table.stateTable.get(currentState).get(transLit)
                        .size(); transition++) {
                    int currentTransition = table.stateTable.get(currentState).get(transLit).get(transition);
                    // if transition isn't free to non-final state
                    if (!(currentTransition != table.getFinalState()
                            && table.inputLits.get(transLit) == SpecialTransitions.freeTransition)) {
                        // exclude duplicates
                        if (!currentStateLits.contains(table.inputLits.get(transLit))) {
                            currentStateLits.add(table.inputLits.get(transLit));
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
        int freeTransLitIndex = table.inputLits.indexOf(SpecialTransitions.freeTransition);
        if (freeTransLitIndex != -1) {
            for (int freeTransition = 0; freeTransition < table.stateTable.get(startState).get(freeTransLitIndex)
                    .size(); freeTransition++) {
                List<Integer> childStates = buildEpsilonClosure(table,
                        table.stateTable.get(startState).get(freeTransLitIndex).get(freeTransition));
                for (Integer state : childStates) {
                    if (!closureStates.contains(state) && (state != table.getFinalState())) {
                        closureStates.add(state);
                    }
                }
            }
        }
        return closureStates;
    }
}