package parser.token;

public enum TokenPrioritiy {
    LITERAL(1), ASTERISK(2), CONCAT(3);

    TokenPrioritiy(int value) {
        this.value = value;
    }

    private int value;

    public int toInt() {
        return this.value;
    }
}