package parser.token;

public class Literal implements Token {
    String value = "";

    @Override
    public int getPriority() {

        return TokenPriority.LITERAL.toInt();
    }
    }

    public Literal(String v) {
        value = v;
    }

    public void apply() {

    }
}