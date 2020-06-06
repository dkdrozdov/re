import java.util.*;

import nfa.StateTable;
import nfa.nfa_builder.NFABuilder;
import parser.*;
import parser.token.*;
import parser.treebuilder.*;

public class Regex {

    public static StateTable buildFA(String regex) {
        return NFABuilder.buildNFA(Treebuilder.buildTree(Parser.parse(regex)));
    }

    public static void main(String[] args) {
        StateTable testTable = buildFA("a*^a");
    }
}