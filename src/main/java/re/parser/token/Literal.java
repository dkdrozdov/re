package re.parser.token;

import java.util.List;

import re.nfa.StateTable;

public class Literal implements Token {
    String value = "";

    @Override
    public int getPriority() {

        return TokenPriority.LITERAL.toInt();
    }

    char getLit() {
        return value.toCharArray()[0];
    }

    public Literal(String v) {
        value = v;
    }

    public StateTable apply(List<StateTable> operands) {
        StateTable table = new StateTable();
        table.addInputLit(value);
        table.addTransition(value, table.getStartState(), table.getFinalState());
        return table;
    }
}