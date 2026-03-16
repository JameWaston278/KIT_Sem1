package message;

/**
 * The ConfigError enum defines a set of error messages related to configuration
 * issues in the game. Each enum constant represents a specific type of
 * configuration error, and the get() method returns the corresponding error
 * message text.
 *
 * @author udqch
 */
public enum ConfigError {

    /** Error message for invalid board lines. */
    INVALID_BOARD_LINES("Board symbol file must contain exactly one line."),
    /** Error message for invalid board symbols. */
    INVALID_BOARD_SYMBOLS("Board symbol line must contain exactly 29 characters."),

    /** Error message for empty unit templates. */
    EMPTY_TEMPLATE("Unit file is empty."),
    /** Error message for negative unit count. */
    UNIT_COUNT_NEGATIVE("Unit count cannot be negative."),
    /** Error message for invalid unit template format. */
    INVALID_UNIT_TEMPLATE("Unit template line must contain exactly 4 parts separated by a comma."),
    /** Error message for too many defined units. */
    TOO_MANY_UNITS("Too many defined units. Maximum allowed is 80."),

    /** Error message for deck lines mismatch. */
    DECK_LINES_MISMATCH("Deck lines mismatch with templates."),
    /** Error message for invalid deck size. */
    INVALID_DECK_SIZE("Deck must contain exactly 40 units.");

    private final String text;

    ConfigError(String text) {
        this.text = text;
    }

    /**
     * Returns the error message text.
     *
     * @return The error message text.
     */
    public String get() {
        return this.text;
    }
}
