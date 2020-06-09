package re.parser.token;

import java.util.List;
import re.nfa.StateTable;

public interface Token {
    // static final int priority = 0;
    public int getPriority();

    public StateTable apply(List<StateTable> operands);
}