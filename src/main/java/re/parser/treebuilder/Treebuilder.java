package re.parser.treebuilder;

import java.util.*;

import re.parser.token.*;

public class Treebuilder {

    public static Node<Token> buildTree(List<Token> tokens) {
        if (tokens.size() == 0) {
            return null;
        }

        /* FIND INDEX OF MAX PRIORITY TOKEN */
        Iterator<Token> tokensIterator = tokens.iterator();
        int highestPriorityTokenIndex = 0;
        int highestPriority = 0;

        while (tokensIterator.hasNext()) {
            Token token = tokensIterator.next();
            if (token.getPriority() > highestPriority) {
                highestPriority = token.getPriority();
                highestPriorityTokenIndex = tokens.indexOf(token);
            }
        }

        Token highestPriorityToken = tokens.get(highestPriorityTokenIndex);
        // Create a node out of first highest-priority token
        Node<Token> n = new Node<Token>(highestPriorityToken);
        if (highestPriorityToken.getType() == TokenType.CAPTURING_GROUP) {
            n = buildTree(((CapturingGroup) highestPriorityToken).getTokens());
        }
        // Launch this function recursive for part by left and right of first
        // highest-priority token of tokens list.
        n.addChild(buildTree(tokens.subList(0, highestPriorityTokenIndex)));
        n.addChild(buildTree(tokens.subList(highestPriorityTokenIndex + 1, tokens.size())));

        return n;
    }

}