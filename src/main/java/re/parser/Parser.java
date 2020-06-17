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
                tokens.addAll(handleBackslash(s, i));
                continue;
            }
            switch (s.charAt(i)) {
                case '.': {
                    int rangeFrom = 0;
                    int rangeTo = 255;
                    Range range = new Range(rangeFrom, rangeTo);
                    List<Token> unfolded = range.unfold();
                    nextToken = new CapturingGroup(new Alteration(unfolded));
                    break;
                }
                case '?': {
                    nextToken = null;
                    parseQuantifier("0,1", tokens);
                    break;
                }
                case '{': {
                    String passed = "";
                    i++;
                    while (s.charAt(i) != '}') {
                        passed = passed.concat(String.valueOf(s.charAt(i)));
                        i++;
                    }
                    nextToken = null;
                    parseQuantifier(passed, tokens);
                    break;
                }
                case '-': {
                    Range range = new Range(s.charAt(i - 1), s.charAt(i + 1));
                    i += 1;
                    tokens.remove(tokens.size() - 1);
                    List<Token> unfolded = range.unfold();
                    for (int j = 0; j < unfolded.size() - 1; j++) {
                        tokens.add(unfolded.get(j));
                    }
                    nextToken = unfolded.get(unfolded.size() - 1);
                    break;
                }
                case '[': {
                    String passed = "";
                    i++;
                    while (s.charAt(i) != ']' || s.substring(i - 1, i + 1).equals(backSlashS.concat("]"))) {
                        passed = passed.concat(String.valueOf(s.charAt(i)));
                        i++;
                    }
                    nextToken = new CapturingGroup((Token) new Alteration(parseNoConcat(passed)));
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
            if (nextToken != null) {
                tokens.add(nextToken);
            }
        }
        return tokens;
    }

    private static List<Token> handleBackslash(String s, int i) {
        List<Token> tokens = new ArrayList<Token>();
        switch (s.charAt(i)) {
            case 'n': {
                List<Token> newToken = new ArrayList<Token>();
                newToken.add(new Literal("\n"));
                tokens.add(new CapturingGroup(newToken));
                break;
            }
            case 't': {
                List<Token> newToken = new ArrayList<Token>();
                newToken.add(new Literal("\t"));
                tokens.add(new CapturingGroup(newToken));
                break;
            }
            case 'w': {
                Range range = new Range('A', 'Z');
                List<Token> unfolded = range.unfold();
                range = new Range('a', 'z');
                unfolded.addAll(range.unfold());
                range = new Range('0', '9');
                unfolded.addAll(range.unfold());
                unfolded.add(new Literal("_"));
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 'W': {
                Range range = new Range(0, 255);
                List<Token> unfolded = range.unfold();
                range = new Range('a', 'z');
                unfolded.removeAll(range.unfold());
                range = new Range('A', 'Z');
                unfolded.removeAll(range.unfold());
                range = new Range('0', '9');
                unfolded.removeAll(range.unfold());
                unfolded.remove(new Literal("_"));
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 'a': {
                Range range = new Range('a', 'z');
                List<Token> unfolded = range.unfold();
                range = new Range('A', 'Z');
                unfolded.addAll(range.unfold());
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 's': {
                List<Token> unfolded = new ArrayList<Token>();
                unfolded.add(new Literal(" "));
                unfolded.add(new Literal("\t"));
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 'd': {
                Range range = new Range('0', '9');
                List<Token> unfolded = range.unfold();
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 'D': {
                Range range = new Range(0, 255);
                List<Token> unfolded = range.unfold();
                range = new Range('0', '9');
                unfolded.removeAll(range.unfold());
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 'l': {
                Range range = new Range('a', 'z');
                List<Token> unfolded = range.unfold();
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 'S': {
                Range range = new Range(0, 255);
                List<Token> unfolded = range.unfold();
                unfolded.remove(new Literal(" "));
                unfolded.remove(new Literal("\t"));
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 'u': {
                Range range = new Range('A', 'Z');
                List<Token> unfolded = range.unfold();
                tokens.add(new Alteration(unfolded));
                break;
            }
            case 'x': {
                Range range = new Range('A', 'F');
                List<Token> unfolded = range.unfold();
                range = new Range('a', 'f');
                unfolded.addAll(range.unfold());
                range = new Range('0', '9');
                unfolded.addAll(range.unfold());
                tokens.add(new Alteration(unfolded));
                break;
            }
            default: {
                tokens.add(new Literal(String.valueOf(s.charAt(i))));
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