/**
 * Enumeration of card ranks (values).
 * Includes label mapping for German card deck (e.g., Bube -> B).
 *
 * @author udqch
 * @version 1.0
 */
public enum Rank {
    /** Sieben */
    SEVEN("7"),
    /** Acht */
    EIGHT("8"),
    /** Neun */
    NINE("9"),
    /** Zehn */
    TEN("10"),
    /** Bube */
    JACK("B"),
    /** Dame */
    QUEEN("D"),
    /** König */
    KING("K"),
    /** Ass */
    ACE("A");

    private final String label;

    /** Constructs a new Rank instance with default configuration */
    Rank(String label) {
        this.label = label;
    }

    /**
     * Returns the string representation (label) of this constant.
     * 
     * @return The label (e.g., "7" or "B").
     */
    @Override
    public String toString() {
        return this.label;
    }
}