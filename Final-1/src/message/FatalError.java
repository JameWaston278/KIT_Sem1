package message;

/**
 * The FatalError enum defines a set of fatal error messages that can occur
 * during the setup phase of the game. Each enum constant represents a specific
 * type of fatal error, and the get() method returns the corresponding error
 * message text.
 *
 * @author udqch
 */
public enum FatalError {

    /** Error message for file reading issues. */
    FILE_ERROR("File reading error: "),

    /** Error message for invalid number format in configuration. */
    INVALID_NUMBER_FORMAT("Invalid number format in configuration."),
    /** Error message for invalid argument format. */
    INVALID_ARGUMENT_FORMAT("Invalid or duplicate argument format."),
    /** Error message for missing mandatory argument. */
    MISSING_MANDATORY_ARGUMENT("Missing mandatory 'seed' or 'units' argument."),
    /** Error message for invalid deck configuration. */
    INVALID_DECK_CONFIGURATION("Invalid deck configuration. Provide either 'deck' OR both 'deck1' and 'deck2'."),
    /** Error message for invalid verbosity level. */
    INVALID_VERBOSITY_LEVEL("Verbosity must be either 'all' or 'compact'.");

    private final String text;

    FatalError(String text) {
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
