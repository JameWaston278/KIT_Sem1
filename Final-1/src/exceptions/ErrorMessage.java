package exceptions;

public enum ErrorMessage {
    KING_CANNOT_BLOCK("The King cannot block."),
    UNIT_ALREADY_MOVED("Unit %s has already moved this turn."),

    CARD_NOT_IN_HAND("Card %s is not in hand."),

    INVALID_COORDINATES("Invalid coordinates: %s."),
    OUT_OF_BOUNDS("Coordinates out of bounds: %s."),
    NO_UNIT_AT_POSITION("No unit at position %s."),

    GAME_OVER("Game over. %s wins!");

    private final String text;
    private static final String ERROR_PREFIX = "Error: ";

    ErrorMessage(String text) {
        this.text = ERROR_PREFIX + text;
    }

    public String get() {
        return text;
    }

    public String format(Object... args) {
        return String.format(this.text, args);
    }
}
