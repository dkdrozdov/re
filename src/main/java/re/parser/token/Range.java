package re.parser.token;

import java.util.List;

import re.fa.StateTable;

public class Range implements Token {
    char charFrom;
    char charTo;

    public Range(char char1, char char2) {
        charFrom = char1;
        charTo = char2;
    }

    public String unfold() {
        String s = "";
        for (int charCode = (int) charFrom; charCode <= (int) charTo; charCode++) {
            s = s.concat(Character.toString((char) charCode));
        }

        return s;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public TokenType getType() {
        return null;
    }

    @Override
    public StateTable apply(List<StateTable> operands) {
        return null;
    }

}