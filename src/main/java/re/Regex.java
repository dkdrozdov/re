package re;

import java.util.List;

import re.fa.StateTable;
import re.nfa.nfa_builder.NFABuilder;
import re.parser.*;
import re.parser.token.Token;
import re.parser.treebuilder.*;

public class Regex {

    public static StateTable buildFA(String regex) {
        List<Token> tokenArray = Parser.parse(regex);
        Node<Token> treeRoot = Treebuilder.buildTree(tokenArray);
        StateTable table = NFABuilder.buildNFA(treeRoot);
        return table;
    }

    public static void main(String[] args) {
        StateTable testTable = buildFA("a^b^c*^a*^c^b*");
    }
}