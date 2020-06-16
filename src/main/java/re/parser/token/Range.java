package re.parser.token;

import java.util.ArrayList;
import java.util.List;

import re.fa.StateTable;

public class Range implements Token {
    char charFrom;
    char charTo;
    int intFrom = -1;
    int intTo = -1;

    public Range(char char1, char char2) {
        charFrom = char1;
        charTo = char2;
    }

    public Range(int i, int j) {
        intFrom = i;
        intTo = j;
    }

    public List<Token> unfold() {
        List<Token> s = new ArrayList<Token>();
        if (intFrom == -1 && intTo == -1) {
            for (int charCode = (int) charFrom; charCode <= (int) charTo; charCode++) {
                s.add(new Literal(Character.toString((char) charCode)));
            }
        } else {
            for (int charCode = intFrom; charCode <= intTo; charCode++) {
                s.add(new Literal(Character.toString((char) charCode)));
            }
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