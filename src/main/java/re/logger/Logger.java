package re.logger;

import java.util.ArrayList;
import java.util.List;

import re.fa.StateTable;

public class Logger {
    static int logLevel = 0;

    public static String extractTable(StateTable table) {
        List<String> extracted = new ArrayList<String>();

        // set up indent:
        String indent = "\t";
        String firstIndent = "\t\t";
        extracted.add("");
        extracted.set(0, extracted.get(0).concat(firstIndent));
        int lastLitIndex = 0;
        int lastLitLength = 0;
        // look over columns
        for (int transLit = 0; transLit < table.transitionLiterals.size(); transLit++) {
            // zero'th row is transitionLiterals
            indent = "\t";
            int row = 0;
            String lit = table.transitionLiterals.get(transLit);
            switch (lit) {
                case "\n": {
                    extracted.set(row, extracted.get(row).concat("\\n"));
                    break;
                }
                case "\t": {
                    extracted.set(row, extracted.get(row).concat("\\t"));
                    break;
                }
                case " ": {
                    extracted.set(row, extracted.get(row).concat("space"));
                    break;
                }
                default: {
                    extracted.set(row, extracted.get(row).concat(lit));
                    break;
                }
            }
            lastLitLength = extracted.get(row).substring(lastLitIndex).length();
            extracted.set(row, extracted.get(row).concat(indent));
            if (lastLitLength < 8) {
                indent = "\t";
            } else if (lastLitLength <= 16) {
                indent = "\t\t";
            } else if (lastLitLength < 24) {
                indent = "\t\t\t";
            }
            lastLitIndex = extracted.get(row).length();
            row++;
            for (int state : table.getRelevantStates()) {
                // if first lit, then set up row
                if (transLit == 0) {
                    extracted.add("");
                    if (table.isFinal(state)) {
                        extracted.set(row, extracted.get(row).concat("*"));
                    }
                    if (table.getStartState() == state) {
                        extracted.set(row, extracted.get(row).concat("->"));
                    }
                    if (table.getDeadState() == state && table.getDeadState() != 0) {
                        extracted.set(row, extracted.get(row).concat("X"));
                    }
                    extracted.set(row, extracted.get(row).concat(String.valueOf(state)));
                    extracted.set(row, extracted.get(row).concat(firstIndent));
                }
                //
                for (int transition : table.stateTable.get(state).get(transLit)) {
                    extracted.set(row, extracted.get(row).concat(String.valueOf(transition)));
                    if (table.stateTable.get(state).get(transLit)
                            .indexOf(transition) != table.stateTable.get(state).get(transLit).size() - 1) {
                        extracted.set(row, extracted.get(row).concat(","));
                    }
                }
                extracted.set(row, extracted.get(row).concat(indent));
                row++;
            }
        }

        // set up result string
        String resultString = "";
        for (String s : extracted) {
            resultString = resultString.concat(s);
            resultString = resultString.concat("\n");
        }
        return resultString;
    }

    public static void setLogLevel(int level) {
        logLevel = level;
    }

    public static void log(String message, int level) {
        if (level <= Logger.logLevel) {
            System.out.println(message);
        }
    }

    public static void log(String message) {
        int level = 1;
        log(message, level);
    }

    public static String extractProperties(StateTable table) {
        String s = "";
        String size = String.valueOf(table.transitionLiterals.size()).concat(" lits x ")
                .concat(String.valueOf(table.stateTable.size())).concat(" states;\n");
        String deleted = String.valueOf(table.deleted.size()).concat(" states deleted.\n");
        s = s.concat(size).concat(deleted);
        return s;
    }

}