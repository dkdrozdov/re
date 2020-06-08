import java.util.List;

import nfa.StateTable;
import nfa.nfa_builder.NFABuilder;
import parser.*;
import parser.token.Token;
import parser.treebuilder.*;

public class Regex {

    public static StateTable buildFA(String regex) {
        List<Token> tokenArray = Parser.parse(regex);
        Node<Token> treeRoot = Treebuilder.buildTree(tokenArray);
        StateTable table = NFABuilder.buildNFA(treeRoot);
        return table;
    }

    public static void main(String[] args) {
        StateTable testTable = buildFA("c^b*");
    }
}