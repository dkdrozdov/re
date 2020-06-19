package re;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import re.acceptor.Acceptor;
import re.acceptor.Acceptor.AcceptorDecision;
import re.dfa.DFAConverter;
import re.fa.StateTable;
import re.logger.Logger;
import re.nfa.nfa_builder.NFABuilder;
import re.parser.*;
import re.parser.token.Token;
import re.parser.treebuilder.*;

public class Regex {

    public static StateTable buildDFA(String regex) {
        List<Token> tokenArray = Parser.parse(regex);
        Logger.log("Building tree...");
        Node<Token> treeRoot = Treebuilder.buildTree(tokenArray);
        Logger.log("Building NFA...");
        StateTable table = NFABuilder.buildNFA(treeRoot);
        // table.eraseDeleted();
        Logger.log("Built a NFA. Table properties:");
        Logger.log(Logger.extractProperties(table));
        Logger.log("Table:");
        Logger.log(Logger.extractTable(table));
        Logger.log("Converting to DFA...");
        table = DFAConverter.convertToDFA(table);
        Logger.log("Built a DFA. Table properties:");
        Logger.log(Logger.extractProperties(table));
        Logger.log("Table:");
        Logger.log(Logger.extractTable(table));
        return table;
    }

    public static StateTable buildDFASystem(List<String> regexes, List<String> names) {
        List<StateTable> tables = new ArrayList<StateTable>();
        // convert regexes to NFA
        Logger.log("Building NFAs...");
        for (String regex : regexes) {
            List<Token> tokenArray = Parser.parse(regex);
            Node<Token> treeRoot = Treebuilder.buildTree(tokenArray);
            tables.add(NFABuilder.buildNFA(treeRoot));
            Logger.log("NFA built. Table properties:");
            Logger.log(Logger.extractProperties(tables.get(tables.size() - 1)));
        }
        // connect regexes
        Logger.log("Connecting NFAs...");
        StateTable keyring = new StateTable();
        int finalState = keyring.getFinalState();
        for (int i = 0; i < tables.size(); i++) {
            StateTable table = tables.get(i);
            int branch = keyring.addState();
            keyring.addFreeTransition(0, branch);
            keyring.setFinalState(branch);
            keyring.concatenateStateTable(table);
            keyring.addFreeTransition(keyring.getFinalState(), finalState);
            keyring.addMetaFinalState(keyring.getFinalState(), names.get(i));
        }
        keyring.setFinalState(finalState);
        Logger.log("NFAs connected. Table properties:");
        Logger.log(Logger.extractProperties(keyring));
        Logger.log("Table:");
        Logger.log(Logger.extractTable(keyring));
        // convert keyring to dfa
        Logger.log("Converting to DFA...");
        StateTable DFATable = DFAConverter.convertToDFA(keyring);
        Logger.log("DFA built. Table properties:");
        Logger.log(Logger.extractProperties(DFATable));
        Logger.log("Table:");
        Logger.log(Logger.extractTable(DFATable));
        return DFATable;
    }

    public static void ioInteractive() {
        Scanner in = new Scanner(System.in);
        // set up regex table
        System.out.print("Enter a regular expression: ");
        String regex = in.nextLine();
        StateTable table = buildDFA(regex);
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

    public static void ioStandard(String[] args) {
        String regex = args[0];
        StateTable table = buildDFA(regex);
        Scanner in = new Scanner(System.in);
        String string = "";
        String currentDecision = "";
        while (in.hasNextLine()) {
            string = in.nextLine();
            AcceptorDecision decision = Acceptor.runStateTable(table, string);
            switch (decision) {
                case ACCEPT: {
                    currentDecision = "ACCEPT";
                    break;
                }
                case DENY: {
                    currentDecision = "DENY";
                    break;
                }
                default: {
                }
            }
            System.out.println(currentDecision);
        }
        in.close();
    }

    public static void ioStandardSystem() {
        Scanner in = new Scanner(System.in);
        // set up regex array and names
        List<String> regexes = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        String regex = in.nextLine();
        while (!regex.equals("exit")) {
            // StateTable table = buildDFA(regex);
            names.add(regex.substring(0, regex.indexOf(": ")));
            regexes.add(regex.substring(regex.indexOf(": ") + 2, regex.length()));
            regex = in.nextLine();
        }
        StateTable table = buildDFASystem(regexes, names);
        // input string and get decision
        String string = "";
        List<List<String>> matched = new ArrayList<List<String>>();
        string = in.nextLine();
        while (!string.equals("exit")) {
            matched = Acceptor.filter(table, string);
            printMatched(matched);
            System.out.println("\n");
            string = in.nextLine();
        }
        in.close();
    }

    public static void ioInteractiveSystem() {
        Scanner in = new Scanner(System.in);
        // set up regex array and names
        List<String> regexes = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        System.out.print("Enter a regular expression: ");
        String regex = in.nextLine();
        while (!regex.equals("exit")) {
            // StateTable table = buildDFA(regex);
            names.add(regex.substring(0, regex.indexOf(": ")));
            regexes.add(regex.substring(regex.indexOf(": ") + 2, regex.length()));
            System.out.print("Enter a regular expression: ");
            regex = in.nextLine();
        }
        StateTable table = buildDFASystem(regexes, names);
        // input string and get decision
        String string = "";
        List<List<String>> matched = new ArrayList<List<String>>();
        System.out.print("Enter string: ");
        string = in.nextLine();
        while (!string.equals("exit")) {
            matched = Acceptor.filter(table, string);
            printMatched(matched);
            System.out.print("Enter string: ");
            string = in.nextLine();
        }
        in.close();
    }

    public static void printMatched(List<List<String>> matched) {
        List<String> words = matched.get(0);
        List<String> names = matched.get(1);
        for (int i = 0; i < words.size(); i++) {
            String currentOutput = "";
            currentOutput = currentOutput.concat(names.get(i));
            currentOutput = currentOutput.concat(": ");
            currentOutput = currentOutput.concat(words.get(i));
            System.out.println(currentOutput);
        }
    }

    public static void main(String[] args) {
        Logger.setLogLevel(1);
        ioInteractiveSystem();
    }
}