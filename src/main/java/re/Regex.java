package re;

import java.util.List;
import java.util.Scanner;

import re.acceptor.Acceptor;
import re.acceptor.Acceptor.AcceptorDecision;
import re.dfa.DFAConverter;
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
        table = DFAConverter.convertToDFA(table);
        return table;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // set up regex table
        System.out.print("Enter a regular expression: ");
        String regex = in.nextLine();
        StateTable table = buildFA(regex);
        // input string and get decision
        String string = "";
        System.out.print("Enter string: ");
        string = in.nextLine();
        while (!string.equals("exit")) {
            AcceptorDecision decision = Acceptor.runStateTable(table, string);
            switch (decision) {
                case ACCEPT: {
                    System.out.print("ACCEPT\n");

                    break;
                }
                case DENY: {
                    System.out.print("DENY\n");

                    break;
                }
            }
            System.out.print("Enter string: ");
            string = in.nextLine();
        }
        in.close();
    }
}