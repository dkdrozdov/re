package re.parser;

import java.util.*;
import re.parser.token.*;

public class Parser {
    public static List<Token> parseNoConcat(String s) {
        List<Token> tokens = new ArrayList<Token>();
        Token nextToken;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '[': {
                    String passed = "";
                    i++;
                    while (s.charAt(i) != ']') {
                        passed = passed.concat(String.valueOf(s.charAt(i)));
                        i++;
                    }
                    nextToken = new CapturingGroup(insert(parseNoConcat(passed), new Alteration()));
                    break;
                }
                case '+': {
                    // repeat last added token
                    tokens.add(tokens.get(tokens.size() - 1));
                    nextToken = new Asterisk();
                    break;
                }
                case '(': {
                    String passed = "";
                    i++;
                    while (s.charAt(i) != ')') {
                        passed = passed.concat(String.valueOf(s.charAt(i)));
                        i++;
                    }
                    nextToken = new CapturingGroup(parse(passed));
                    break;
                }
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
        return tokens;
    }

    public static List<Token> parse(String s) {
        return insert(parseNoConcat(s), new Concat());
    }

    public static Token createTokenOfTokenType(Token token) {
        TokenType tokenType = token.getType();
        Token newToken = null;
        switch (tokenType) {
            case ALTERATION: {
                newToken = new Alteration();
                break;
            }
            case CONCAT: {
                newToken = new Concat();
                break;
            }
            default: {
            }
        }
        return newToken;
    }

    public static List<Token> insert(List<Token> initialTokens, Token toInsert) {
        List<Token> tokens = new ArrayList<Token>();

        for (int i = 0; i < initialTokens.size(); i++) {
            tokens.add(initialTokens.get(i));
            if (i != initialTokens.size() - 1) {
                TokenType tokenType = initialTokens.get(i).getType();
                switch (tokenType) {
                    case LITERAL: {
                        if (tokenAllowsInserting(initialTokens.get(i + 1))) {
                            tokens.add(createTokenOfTokenType(toInsert));
                        }
                        break;
                    }
                    case ASTERISK: {
                        if (tokenAllowsInserting(initialTokens.get(i + 1))) {
                            tokens.add(createTokenOfTokenType(toInsert));
                        }
                        break;
                    }
                    case CAPTURING_GROUP: {
                        if (tokenAllowsInserting(initialTokens.get(i + 1))) {
                            tokens.add(createTokenOfTokenType(toInsert));
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

    public static boolean tokenAllowsInserting(Token token) {
        if (token.getType() == TokenType.LITERAL) {
            return true;
        }
        if (token.getType() == TokenType.CAPTURING_GROUP) {
            return true;
        }
        return false;
    }
}