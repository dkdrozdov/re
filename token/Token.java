package token;

import java.util.*;

public class Token {
    TokenType type;
    String value;
    int priority;

    public Token(TokenType t, String v) {
        type = t;
        value = v;
    }

    public void apply() {

    }
}