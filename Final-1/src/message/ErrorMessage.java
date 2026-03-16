package message;

/**
 * The ErrorMessage enum defines a set of standardized error messages for
 * various game logic exceptions. Each enum constant represents a specific error
 * scenario that can occur during the game, such as invalid moves, out-of-bounds
 * coordinates, or game over conditions. The enum provides methods to retrieve
 * the error message text and to format messages with dynamic content.
 * 
 * @author udqch
 */
public enum ErrorMessage {
    // --- UNIT ERRORS ---
    /** Error message for when a unit has already moved this turn. */
    UNIT_ALREADY_MOVED("Unit %s has already moved this turn."),
    /** Error message for when a unit has already flipped this turn. */
    UNIT_ALREADY_FLIPPED("Unit %s has already flipped this turn."),
    /** Error message for when a unit can not be selected. */
    INVALID_UNIT("Invalid unit selected."),
    /** Error message for when a unit has already placed a unit this turn. */
    ALREADY_PLACED_UNIT("A unit has already been placed this turn."),
    /** Error message for when a unit is placed on a non-adjacent position. */
    INVALID_PLACEMENT("Target position %s is not adjacent to the King."),
    /** Error message for when a unit is placed on an occupied position. */
    PLACE_ON_ENEMY_UNIT("Target position %s is occupied by an enemy unit."),
    /** Error message for when a unit is placed on an occupied position. */
    INVALID_CARD_INDEX("Card index %d is out of bounds."),
    /** Error message for when a card index is duplicated in hand. */
    DUPLICATE_CARD_INDEX("Duplicate card index %d in hand."),

    // --- KING ERRORS ---
    /**
     * Error message for when a unit tries to attack but has already attacked this
     * turn.
     */
    KING_CANNOT_DUEL("The King cannot attack or be attacked."),
    /**
     * Error message for when a unit tries to block but has already blocked this
     * turn.
     */
    KING_CANNOT_BLOCK("The King cannot block."),

    // --- HAND ERRORS ---
    /** Error message for when a card is not in the player's hand. */
    CARD_NOT_IN_HAND("Card %s is not in hand."),
    /**
     * Error message for when a player's hand is full and cannot draw more cards.
     */
    HAND_FULL("%s's hand is full!"),
    /**
     * Error message for when a player's hand is not full and does not need to
     * discard.
     */
    HAND_NOT_FULL("%s's hand is not full! No card needs to be discarded."),

    // --- TURN AND GAME STATE ERRORS ---
    /** Error message for when a player tries to perform an action out of turn. */
    WRONG_TURN("It's not %s's turn."),
    /**
     * Error message for when a player tries to perform an action that is not
     * allowed in the current game state.
     */
    INVALID_COORDINATES("Invalid coordinates: %s."),
    /**
     * Error message for when a player tries to perform an invalid move action.
     */
    INVALID_MOVE_DISTANCE("Invalid move from %s to %s (exceeds movement range)."),
    /**
     * Error message for when a player tries to perform an action on a position that
     * is out of bounds.
     */
    OUT_OF_BOUNDS("Coordinates out of bounds: %s."),
    /**
     * Error message for when a player tries to perform an action on a position that
     * is not occupied by a unit.
     */
    NO_UNIT_AT_POSITION("No unit at position %s.");

    private final String text;

    ErrorMessage(String text) {
        this.text = text;
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
