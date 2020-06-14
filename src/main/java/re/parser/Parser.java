package re.parser;

import java.util.*;
import re.parser.token.*;

public class Parser {
    public static List<Token> parse(String s) {
        List<Token> tokens = new ArrayList<Token>();
        Token nextToken;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '*': {
                    nextToken = new Asterisk();
                    break;
                }
                case '|': {
                    nextToken = new Alteration();
                    break;
                }
                default: {
                    nextToken = new Literal(String.valueOf(s.charAt(i)));
                }
            }
            tokens.add(nextToken);
        }
        tokens = insertConcatenations(tokens);
        return tokens;
    }

    public static List<Token> insertConcatenations(List<Token> initialTokens) {
        List<Token> tokens = new ArrayList<Token>();

        for (int i = 0; i < initialTokens.size(); i++) {
            tokens.add(initialTokens.get(i));
            if (i != initialTokens.size() - 1) {
                TokenType tokenType = initialTokens.get(i).getType();
                switch (tokenType) {
                    case LITERAL: {
                        if (initialTokens.get(i + 1).getType() == TokenType.LITERAL) {
                            tokens.add(new Concat());
                        }
                        break;
                    }
                    case ASTERISK: {
                        if (initialTokens.get(i + 1).getType() == TokenType.LITERAL) {
                            tokens.add(new Concat());
                        }
                        break;
                    }
                    default: {

                    }
                }
            }
        }

        return tokens;
    }
}