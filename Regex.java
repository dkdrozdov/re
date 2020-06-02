import java.util.*;
import parser.*;
import parser.token.*;
import parser.treebuilder.*;

public class Regex {
    public static void main(String[] args) {
        ArrayList<Token> testTokens = Parser.parse("a*^b*^a^a^a^a");
        Node<Token> testTree = Treebuilder.buildTree(testTokens);
        // Scanner in = new Scanner(System.in);
        // String s = in.nextLine();
        // System.out.println("You entered string " + s);
    }
}