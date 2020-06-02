package parser.treebuilder;

import java.util.*;
import parser.token.Token;

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
        /*
         * tokens.forEach(token -> { Token a = tokensIterator.next(); if
         * (token.getPriority() > highestPriority) { highestPriority =
         * token.getPriority(); highestPriorityTokenIndex = tokens.indexOf(token); } });
         */
        /*
         * ArrayList<Integer> prioritizedTokensIndexes = new ArrayList<Integer>(); for
         * (int i = 0; i < tokens.size(); i++) { prioritizedTokensIndexes.add(i); }
         * 
         * Collections.sort(prioritizedTokensIndexes, (Integer lhs, Integer rhs) ->
         * tokens.get(lhs).getPriority() - tokens.get(rhs).getPriority());
         * 
         * int highestPriorityTokenIndex = prioritizedTokensIndexes.get(0);
         */

        // Create a node out of first highest-priority token
        Node<Token> n = new Node<Token>(tokens.get(highestPriorityTokenIndex));
        // Launch this function recursive for part by left and right of first
        // highest-priority token of tokens list.
        n.addChild(buildTree(tokens.subList(0, highestPriorityTokenIndex)));
        n.addChild(buildTree(tokens.subList(highestPriorityTokenIndex + 1, tokens.size())));

        return n;
    }

    public static ArrayList<Token> structurizeTokens(ArrayList<Token> tokens) {
        // 1. Insert concats
        for (int i = 0; i < tokens.size(); i++) {

        }

        // 2. Insert tokens into each other
        return null;
    }
}