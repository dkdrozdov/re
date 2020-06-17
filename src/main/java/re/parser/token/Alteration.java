package re.parser.token;

import java.util.ArrayList;
import java.util.List;

import re.fa.StateTable;

public class Alteration implements Token {
    private List<Token> tokens = null;

    public List<Token> getOperands() {
        return tokens;
    }

    public Alteration() {
    }

    public Alteration(List<Token> o) {
        tokens = new ArrayList<Token>(o);
    }

    @Override
    public TokenType getType() {
        return TokenType.ALTERATION;
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            Alteration q = (Alteration) obj;
            return this.getType() == q.getType();
        }
        return false;
    }

    public StateTable apply(List<StateTable> operands) {
        StateTable table = new StateTable();
        int finalState = table.getFinalState();
        for (int i = 0; i < operands.size(); i++) {
            StateTable currentTable = operands.get(i);
            // int branch = table.addState();
            // table.addFreeTransition(table.getStartState(), branch);
            table.setFinalState(table.getStartState());
            table.concatenateStateTable(currentTable);
            table.addFreeTransition(table.getFinalState(), finalState);
        }
        table.setFinalState(finalState);

        // StateTable table = new StateTable();
        // int firstBranch = 1;
        // table.addFreeTransition(0, firstBranch);
        // table.setFinalState(firstBranch);
        // table.concatenateStateTable(operands.get(0));
        // int metaFinal = table.getFinalState();
        // int secondBranch = table.addState();
        // table.addFreeTransition(0, secondBranch);
        // table.setFinalState(secondBranch);
        // table.concatenateStateTable(operands.get(1));
        // int newFinal = table.addState();
        // table.addFreeTransition(metaFinal, newFinal);
        // table.addFreeTransition(table.getFinalState(), newFinal);
        // table.setFinalState(newFinal);

        return table;
    }

    @Override
    public int getPriority() {
        return TokenPriority.ALTERATION.toInt();
    }

    public boolean isPlural() {
        return tokens != null;
    }
}