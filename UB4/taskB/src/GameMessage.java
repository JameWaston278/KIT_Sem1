public enum GameMessage {

    UNKNOWN_COMMAND("unknown command."),
    REQUIRE_INTEGER("This function requires a numeric parameter."),
    TOO_LARGE_NUMBER("Your input number is too large."),

    // --- System / Status error ---
    GAME_ENDED("Game has ended and cannot be restarted."),
    INVALID_PLAYER_COUNT("Invalid player ID. Expected 1-%d."),
    SHOW_GAME("%s / %d"),
    SHOW("%s"),

    // --- Game turn error ---
    WRONG_TURN("It is not player %d's turn."),

    // --- Processing card error ---
    CARD_NOT_FOUND("Player %d does not have card %s."),
    INVALID_MOVE("Card %s cannot be played on %s."),

    // --- End game notification ---
    GAME_WON("Game over: Player %d has won."),
    GAME_DRAW("Game over: Draw.");

    private final String pattern;

    GameMessage(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Formats the message with the provided arguments.
     * * @param args The arguments to replace placeholders (e.g., %d, %s).
     * 
     * @return The formatted string.
     */
    public String format(Object... args) {
        return String.format(pattern, args);
    }

    @Override
    public String toString() {
        return pattern;
    }
}