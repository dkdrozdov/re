package re.acceptor;

import java.util.ArrayList;
import java.util.List;

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
            return table.getTransition(currentState, literalIndex, 0);
            // return table.stateTable.get(currentState).get(literalIndex).get(0);
        } else {
            int indexOfOtherTransition = table.transitionLiterals.indexOf(SpecialTransitions.otherTransition);
            return table.getTransition(currentState, indexOfOtherTransition, 0);
        }
    }

    public static List<List<String>> filter(StateTable table, String inputString) {
        List<List<String>> filtered = new ArrayList<List<String>>();
        List<String> matchedWords = new ArrayList<String>();
        List<String> matchedNames = new ArrayList<String>();

        int currentState = table.getStartState();
        String currentMatch = "";
        String previousName = null;
        char[] inputArray = inputString.toCharArray();
        for (int i = 0; i < inputString.length(); i++) {
            String inputLiteral = String.valueOf(inputArray[i]);
            currentMatch = currentMatch.concat(inputLiteral);
            currentState = feedTable(table, inputLiteral, currentState);
            if (table.metaFinalStates.contains(feedTable(table, SpecialTransitions.freeTransition, currentState))) {
                currentState = feedTable(table, SpecialTransitions.freeTransition, currentState);
            }
            if (table.isFinal(currentState)) {
                if (table.getFinalState() == currentState) {
                    matchedNames.add("no info");
                } else {
                    int metaFinalIndex = table.metaFinalStates.indexOf(currentState);
                    matchedNames.add(table.metaFinalStatesNames.get(metaFinalIndex));
                }
                if (previousName != null) {
                    if (!matchedNames.get(matchedNames.size() - 1).equals(previousName)) {
                        matchedWords.add(new String(currentMatch));
                    } else {
                        matchedNames.remove(matchedNames.size() - 1);
                        matchedWords.set(matchedWords.size() - 1,
                                matchedWords.get(matchedWords.size() - 1).concat(currentMatch));
                    }
                } else {
                    matchedWords.add(new String(currentMatch));
                }
                previousName = matchedNames.get(matchedNames.size() - 1);
                currentMatch = "";
                currentState = table.getStartState();
            }
            if (table.getDeadState() == currentState) {
                if (currentMatch.length() != 1) {
                    i--;
                }
                previousName = null;
                currentMatch = "";
                currentState = table.getStartState();
            }
        }
        filtered.add(matchedWords);
        filtered.add(matchedNames);
        return filtered;
    }
}