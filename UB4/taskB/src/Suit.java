/**
 * Enumeration of card suits.
 * Includes label mapping for German card deck (e.g., Herz -> H).
 *
 * @author udqch
 * @version 1.0
 */
public enum Suit {
    /** Eichel */
    EICHEL("E"),
    /** Laub */
    LAUB("L"),
    /** Herz */
    HERZ("H"),
    /** Schellen */
    SCHELLEN("S");

    private final String label;

    /** Constructs a new Suit instance with default configuration */
    Suit(String label) {
        this.label = label;
    }

    /**
     * Returns the string representation (label) of this constant.
     * 
     * @return The label (e.g., "K" or "H").
     */
    @Override
    public String toString() {
        return this.label;
    }
}