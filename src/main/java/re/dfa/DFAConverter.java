package re.dfa;

import java.util.ArrayList;
import java.util.List;

import re.fa.SpecialTransitions;
import re.fa.StateTable;

public class DFAConverter {
    public static StateTable convertToDFA(StateTable table) {
        StateTable DFATable = new StateTable();
        DFATable = eliminateNonDeterminism(table, table.getStartState());
        return new StateTable();
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
                    if (!closureStates.contains(state)) {
                        closureStates.add(state);
                    }
                }
            }
        }
        return closureStates;
    }

    public static StateTable eliminateNonDeterminism(StateTable table, int startState) {
        List<Integer> states = new ArrayList<Integer>();
        // add in states list startState and all states which startState lead to through
        // free transition
        states = buildEpsilonClosure(table, startState);

        return null;
    }
}