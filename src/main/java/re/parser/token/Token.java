package re.parser.token;

import java.util.List;

import re.fa.StateTable;

public interface Token {
    // static final int priority = 0;
    public int getPriority();

    public StateTable apply(List<StateTable> operands);
}