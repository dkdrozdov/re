package re.parser.token;

import java.util.List;

import javax.management.openmbean.OpenType;

import re.fa.StateTable;

public class Alteration implements Token {
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
        int firstBranch = 1;
        table.addFreeTransition(0, firstBranch);
        table.setFinalState(firstBranch);
        table.concatenateStateTable(operands.get(0));
        int metaFinal = table.getFinalState();
        int secondBranch = table.addState();
        table.addFreeTransition(0, secondBranch);
        table.setFinalState(secondBranch);
        table.concatenateStateTable(operands.get(1));
        int newFinal = table.addState();
        table.addFreeTransition(metaFinal, newFinal);
        table.addFreeTransition(table.getFinalState(), newFinal);
        table.setFinalState(newFinal);

        return table;
    }

    @Override
    public int getPriority() {
        return TokenPriority.ALTERATION.toInt();
    }
}