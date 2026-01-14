public enum GameMessage {

    UNKNOWN_COMMAND("unknown command."),
    REQUIRE_NUMBER("this function requires a numeric parameter."),
    TOO_LARGE_NUMBER("your input number is too large."),
    NO_PARAMETER("this command accepts no parameters."),
    INVALID_ARGUMENT_COUNT("invalid number of arguments. Expected %d but got %d."),

    // --- System / Status error ---
    GAME_ENDED("game has ended and cannot be restarted."),
    MATCH_ENDED("match has ended, only \"start\" and \"quit\" commands allow."),
    INVALID_PLAYER_COUNT("invalid player ID, expected 1-%d."),
    SHOW_GAME("%s / %d"),
    SHOW_PLAYER_DECK("%s"),

    // --- Game turn error ---
    WRONG_TURN("it is not player %d's turn."),

    // --- Processing card error ---
    CARD_NOT_FOUND("player %d does not have card %s."),
    INVALID_MOVE("card %s cannot be played on %s."),

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