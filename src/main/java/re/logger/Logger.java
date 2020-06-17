package re.logger;

import re.fa.StateTable;

public class Logger {
    static int logLevel = 0;

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