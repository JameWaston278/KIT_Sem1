package exceptions;

/**
 * The ErrorMessage enum defines a set of standardized error messages for
 * various
 * game logic exceptions. Each enum constant represents a specific error
 * scenario
 * that can occur during the game, such as invalid moves, out-of-bounds
 * coordinates, or game over conditions. The enum provides methods to retrieve
 * the error message text and to format messages with dynamic content.
 * 
 * @author udqch
 */
public enum ErrorMessage {
    UNIT_ALREADY_MOVED("Unit %s has already moved this turn."),

    KING_CANNOT_DUEL("The King cannot attack or be attacked."),
    KING_CANNOT_BLOCK("The King cannot block."),

    CARD_NOT_IN_HAND("Card %s is not in hand."),
    HAND_FULL("%s's hand is full!"),
    HAND_NOT_FULL("%s's hand is not full! No card needs to be discarded."),

    WRONG_TURN("It's not %s's turn."),

    INVALID_COORDINATES("Invalid coordinates: %s."),
    OUT_OF_BOUNDS("Coordinates out of bounds: %s."),
    NO_UNIT_AT_POSITION("No unit at position %s."),
    INVALID_UNIT("Invalid unit selected."),

    GAME_OVER("Game over. %s wins!");

    private static final String ERROR_PREFIX = "ERROR: ";

    private final String text;

    ErrorMessage(String text) {
        this.text = ERROR_PREFIX + text;
    }

    /**
     * Formats the error message with the provided arguments, if applicable.
     *
     * @param args The arguments to format into the error message.
     * @return The formatted error message.
     */
    public String format(Object... args) {
        return String.format(this.text, args);
    }
}
