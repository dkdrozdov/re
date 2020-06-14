package re.parser.token;

import java.util.List;

import re.fa.StateTable;

public class Concat implements Token {
    @Override
    public TokenType getType() {
        return TokenType.CONCAT;
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            Concat q = (Concat) obj;
            return this.getType() == q.getType();
        }
        return false;
    }

    @Override
    public int getPriority() {

        return TokenPriority.CONCAT.toInt();
    }

    public StateTable apply(List<StateTable> operands) {
        StateTable table = operands.get(0);
        table.concatenateStateTable(operands.get(1));
        return table;
    }
}