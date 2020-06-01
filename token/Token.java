package token;

import java.util.*;

enum TokenType {
    star, lit, concat, alter
}

class Token {
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