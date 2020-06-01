import java.util.*;

public class Regex {

    static ArrayList<Token> parse(String s) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        Token nextToken;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '*': {
                    nextToken = new Token(TokenType.star, "");
                    break;
                }
                default: {
                    nextToken = new Token(TokenType.lit, String.valueOf(s.charAt(i)));
                }
            }
            tokens.add(nextToken);
        }
        return tokens;
    }

    public static void main(String[] args) {
        ArrayList<Token> testtokens = parse("ab*");

        // Scanner in = new Scanner(System.in);
        // String s = in.nextLine();
        // System.out.println("You entered string " + s);
    }
}