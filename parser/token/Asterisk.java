package parser.token;

public class Asterisk implements Token {
    public void apply() {

    }

    @Override
    public int getPriority() {
        return TokenPriority.ASTERISK.toInt();
    }
}