package re.parser.token;

public enum TokenPriority {
    LITERAL(1), ASTERISK(2), CONCAT(3), ALTERATION(4);

    TokenPriority(int value) {
        this.value = value;
    }

    private int value;

    public int toInt() {
        return this.value;
    }
}