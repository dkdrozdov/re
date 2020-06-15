package re.parser.token;

import java.util.List;

import re.fa.StateTable;

public class CapturingGroup implements Token {
    List<Token> tokensGroup = null;

    public CapturingGroup(List<Token> tokens) {
        tokensGroup = tokens;
    }

    public CapturingGroup(CapturingGroup group) {
        tokensGroup = group.getTokens();
    }

    @Override
    public TokenType getType() {
        return TokenType.CAPTURING_GROUP;
    }

    public List<Token> getTokens() {
        return tokensGroup;
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            CapturingGroup q = (CapturingGroup) obj;
            return this.getType() == q.getType();
        }
        return false;
    }

    public StateTable apply(List<StateTable> operands) {

        return null;
    }

    @Override
    public int getPriority() {
        return TokenPriority.CAPTURING_GROUP.toInt();
    }
}