package re.parser.token;

import java.util.List;

import re.fa.StateTable;

public class Literal implements Token {

    String value = "";

    @Override
    public TokenType getType() {
        return TokenType.LITERAL;
    }
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
        table.addTransitionLiteral(value);
        table.addTransition(value, table.getStartState(), table.getFinalState());
        return table;
    }
}