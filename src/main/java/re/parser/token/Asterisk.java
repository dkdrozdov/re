package re.parser.token;

import java.util.List;

import re.fa.StateTable;

public class Asterisk implements Token {
    @Override
    public TokenType getType() {
        return TokenType.ASTERISK;
    }
    public StateTable apply(List<StateTable> operands) {
        StateTable table = new StateTable(operands.get(0));
        // int oldFinalState = table.getFinalState();
        int metaFinal = table.getFinalState();
        table.concatenateStateTable(operands.get(0));
        // table.removeState(table.getFinalState());
        table.replaceStateOnlyTable(table.getFinalState(), metaFinal);
        table.addFreeTransition(metaFinal, table.getFinalState());
        table.addFreeTransition(table.getStartState(), table.getFinalState());
        return table;
    }

    @Override
    public int getPriority() {
        return TokenPriority.ASTERISK.toInt();
    }
}