package parser.token;

public class Concat implements Token {
    @Override
    public int getPriority() {

        return TokenPrioritiy.CONCAT.toInt();
    }

    public void apply() {

    }
}