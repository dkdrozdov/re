package parser.token;

public class Literal implements Token {
    static final int priority = 4;
    String value = "";

    public Literal(String v) {
        value = v;
    }

    public void apply() {

    }
}