public enum Rank {
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("B"), // Bube
    QUEEN("D"), // Dame
    KING("K"), // König
    ACE("A"); // Ass

    private final String label;

    private Rank(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
