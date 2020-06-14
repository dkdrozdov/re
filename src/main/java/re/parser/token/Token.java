package re.parser.token;

import java.util.List;

import re.fa.StateTable;

public interface Token {
    public int getPriority();

    public TokenType getType();

    public StateTable apply(List<StateTable> operands);
}