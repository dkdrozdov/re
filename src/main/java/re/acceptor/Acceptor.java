package re.acceptor;

import re.fa.SpecialTransitions;
import re.fa.StateTable;

public class Acceptor {
    public enum AcceptorDecision {
        ACCEPT, DENY
    }

    public static AcceptorDecision runStateTable(StateTable table, String inputString) {
        int currentState = table.getStartState();
        char[] inputArray = inputString.toCharArray();
        for (int i = 0; i < inputString.length(); i++) {
            String inputLiteral = String.valueOf(inputArray[i]);
            currentState = feedTable(table, inputLiteral, currentState);
        }
        currentState = feedTable(table, SpecialTransitions.freeTransition, currentState);
        return makeDecision(table, currentState);
    }

    private static AcceptorDecision makeDecision(StateTable table, int currentState) {
        if (currentState == table.getFinalState()) {
            return AcceptorDecision.ACCEPT;
        } else {
            return AcceptorDecision.DENY;
        }

    }

    public static int feedTable(StateTable table, String inputLiteral, int currentState) {
        if (table.transitionLiterals.contains(inputLiteral)) {
            int literalIndex = table.transitionLiterals.indexOf(inputLiteral);
            return table.stateTable.get(currentState).get(literalIndex).get(0);
        } else {
            int indexOfOtherTransition = table.transitionLiterals.indexOf(SpecialTransitions.otherTransition);
            return table.stateTable.get(currentState).get(indexOfOtherTransition).get(0);
        }
    }
}