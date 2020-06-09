package re.parser;

import java.util.*;
import re.parser.token.*;

public class Parser {
    public static ArrayList<Token> parse(String s) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        Token nextToken;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '*': {
                    nextToken = new Asterisk();
                    break;
                }
                case '^': {
                    nextToken = new Concat();
                    break;
                }
                default: {
                    nextToken = new Literal(String.valueOf(s.charAt(i)));
                }
            }
            tokens.add(nextToken);
        }
        return tokens;
    }
}