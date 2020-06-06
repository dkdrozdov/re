package nfa.nfa_builder;

import java.util.ArrayList;
import java.util.List;

import nfa.StateTable;
import parser.token.Literal;
import parser.token.Token;
import parser.treebuilder.Node;
import parser.token.TokenPriority;

public class NFABuilder {
    public static StateTable buildNFA(Node<Token> tree) {
        // StateTable table = new StateTable();
        /*
         * Node<Token> leaf = tree.findFirstLeaf(); try { if
         * (leaf.getData().getPriority() != TokenPriority.LITERAL.toInt()) { throw new
         * Exception("The leaf is not a literal"); } } catch (Exception e) { }
         */
        if (tree.getChildren().size() == 0) {
            return tree.getData().apply(null);
        }

        // buildNFA from each of tree.children and put results to list of operands
        List<StateTable> operands = new ArrayList<StateTable>();
        tree.getChildren().forEach(child -> {
            operands.add(buildNFA(child));
        });
        return tree.getData().apply(operands);
    }
}