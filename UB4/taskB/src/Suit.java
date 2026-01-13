public enum Suit {
    EICHEL("E"),
    LAUB("L"),
    HERZ("H"),
    SCHELLEN("S");

    private final String label;

    private Suit(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
