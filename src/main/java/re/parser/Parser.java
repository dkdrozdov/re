package re.parser;

import java.util.*;

import re.fa.SpecialTransitions;
import re.parser.token.*;

public class Parser {
    public static List<Token> parseNoConcat(String s) {
        List<Token> tokens = new ArrayList<Token>();
        Token nextToken;
        String backSlashS = "\\";
        char backSlash = backSlashS.toCharArray()[0];
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == backSlash) {
                i++;
                tokens.add(new Literal(String.valueOf(s.charAt(i))));
                continue;
            }
            switch (s.charAt(i)) {
                case '?': {
                    nextToken = null;
                    parseQuantifier("0,1", tokens);
                    break;
                }
                case '{': {
                    String passed = "";
                    i++;
                    while (s.charAt(i) != '}') {
                        if (s.charAt(i) == backSlash) {
                            passed = passed.concat(String.valueOf(s.charAt(i)));
                            i++;
                            passed = passed.concat(String.valueOf(s.charAt(i)));
                            i++;
                            continue;
                        }
                        passed = passed.concat(String.valueOf(s.charAt(i)));
                        i++;
                    }
                    nextToken = null;
                    parseQuantifier(passed, tokens);
                    break;
                }
                case '-': {
                    Range range = new Range(s.charAt(i - 1), s.charAt(i + 1));
                    i += 2;
                    tokens.remove(tokens.size() - 1);
                    List<Token> unfolded = parseNoConcat(range.unfold());
                    for (int j = 0; j < unfolded.size() - 1; j++) {
                        tokens.add(unfolded.get(j));
                    }
                    nextToken = unfolded.get(unfolded.size() - 1);
                    break;
                }
                case '[': {
                    String passed = "";
                    i++;
                    while (s.charAt(i) != ']') {
                        if (s.charAt(i) == backSlash) {
                            passed = passed.concat(String.valueOf(s.charAt(i)));
                            i++;
                            passed = passed.concat(String.valueOf(s.charAt(i)));
                            i++;
                            continue;
                        }
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
                        if (s.charAt(i) == backSlash) {
                            passed = passed.concat(String.valueOf(s.charAt(i)));
                            i++;
                            passed = passed.concat(String.valueOf(s.charAt(i)));
                            i++;
                            continue;
                        }
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
            if (nextToken != null) {
                tokens.add(nextToken);
            }
        }
        return tokens;
    }

    private static void parseQuantifier(String insides, List<Token> relatedTokens) {
        Token relatedToken = relatedTokens.get(relatedTokens.size() - 1);
        relatedTokens.remove(relatedTokens.size() - 1);
        CapturingGroup parsedGroup;
        List<Token> parsed = new ArrayList<Token>();
        // set up insidesList
        List<String> insidesList = new ArrayList<String>();
        char[] insidesArray = insides.toCharArray();
        for (char character : insidesArray) {
            insidesList.add(String.valueOf(character));
        }

        if (insidesList.contains(",")) {
            int commaIndex = insidesList.indexOf(",");
            // up-unlimited range
            if (commaIndex == insidesList.size() - 1) {
                int from = Integer.parseInt(insides.substring(0, commaIndex));
                for (int i = 0; i <= from; i++) {
                    parsed.add(relatedToken);
                }
                parsed.add(new Asterisk());
                parsedGroup = new CapturingGroup(insert(parsed, new Concat()));
                relatedTokens.add(parsedGroup);
                return;
            }
            // down-unlimited range
            if (commaIndex == 0) {
                // reduce the problem to limited range
                relatedTokens.add(relatedToken);
                parseQuantifier("0".concat(",").concat(insides.substring(commaIndex + 1, insidesList.size())),
                        relatedTokens);
                return;
            }
            // case limited range
            int from = Integer.parseInt(insides.substring(0, commaIndex));
            int to = Integer.parseInt(insides.substring(commaIndex + 1, insidesList.size()));
            for (int i = from; i <= to; i++) {
                parsed.add(new CapturingGroup((parseFixedQuantifier(String.valueOf(i), relatedToken))));
            }
            relatedTokens.add(new CapturingGroup(insert(parsed, new Alteration())));
            return;

        } else { // 3. case fixed number
            parsedGroup = parseFixedQuantifier(insides, relatedToken);
            relatedTokens.add(parsedGroup);
            return;
        }
    }

    private static CapturingGroup parseFixedQuantifier(String insides, Token relatedToken) {
        List<Token> parsed = new ArrayList<Token>();
        int to = Integer.parseInt(insides);
        if (to == 0) {
            parsed.add(new Literal(SpecialTransitions.freeTransition));
        }
        for (int i = 0; i < to; i++) {
            parsed.add(relatedToken);
        }
        parsed = insert(parsed, new Concat());
        return new CapturingGroup(parsed);
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