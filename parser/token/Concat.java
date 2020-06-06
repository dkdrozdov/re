package parser.token;

public class Concat implements Token {
    @Override
    public int getPriority() {

        return TokenPriority.CONCAT.toInt();
    }

    public void apply() {

    }
}