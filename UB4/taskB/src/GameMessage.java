/**
 * Centralizes all game messages and error notifications.
 *
 * @author udqch
 * @version 1.0
 */
public enum GameMessage {

    // --- INPUT & PARSING ERRORS ---

    /** Unknown command entered. */
    UNKNOWN_COMMAND("unknown command."),

    /** Command requires a numeric parameter but got text. */
    REQUIRE_NUMBER("this function requires a numeric parameter."),

    /** Input number exceeds integer limits. */
    TOO_LARGE_NUMBER("your input number is too large."),

    /** Command expects no parameters but got some. */
    NO_PARAMETER("this command accepts no parameters."),

    /**
     * Argument count mismatch.
     * Args: %d (expected count), %d (actual count).
     */
    INVALID_ARGUMENT_COUNT("invalid number of arguments. Expected %d but got %d."),

    // --- GAME STATE ERRORS ---

    /** Action attempted after game has permanently ended. */
    GAME_ENDED("game has ended and cannot be restarted."),

    /** Action attempted after round ended (only start/quit allowed). */
    MATCH_ENDED("match has ended, only \"start\" and \"quit\" commands allow."),

    /**
     * Player ID out of range.
     * Arg: %d (max ID allowed).
     */
    INVALID_PLAYER_ID("invalid player ID, expected 1-%d."),

    /**
     * Action attempted by wrong player.
     * Arg: %d (expected player ID).
     */
    WRONG_TURN("it is not player %d's turn."),

    // --- LOGIC & RULE ERRORS ---

    /**
     * Player does not hold the specified card.
     * Args: %d (Player ID), %s (Card Name).
     */
    CARD_NOT_FOUND("player %d does not have card %s."),

    /**
     * Invalid card move (rank/suit mismatch).
     * Args: %s (Card played), %s (Target card).
     */
    INVALID_MOVE("card %s cannot be played on %s."),

    // --- INFO & OUTPUT FORMATS ---

    /**
     * Game status format.
     * Args: %s (Top card), %d (Draw pile size).
     */
    SHOW_GAME("%s / %d"),

    /**
     * Player deck format.
     * Arg: %s (Deck string representation).
     */
    SHOW_PLAYER_DECK("%s"),

    // --- GAME OUTCOMES ---

    /**
     * Player won notification.
     * Arg: %d (Winning Player ID).
     */
    GAME_WON("Game over: Player %d has won."),

    /** Game ended in a draw. */
    GAME_DRAW("Game over: Draw.");

    private final String pattern;

    GameMessage(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Formats the message pattern with given arguments.
     * 
     * @param args Arguments for placeholders (%d, %s).
     * @return Formatted string.
     */
    public String format(Object... args) {
        return String.format(pattern, args);
    }

    /**
     * Returns the string pattern of this constant.
     * 
     * @return The pattern (e.g., "Game over: Draw").
     */

    @Override
    public String toString() {
        return pattern;
    }
}