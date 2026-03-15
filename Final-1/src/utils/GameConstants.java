package utils;

/**
 * The GameConstants class defines various constant values that are used
 * throughout the game.
 * 
 * @author udqch
 */
public final class GameConstants {

    /** Maximum size of a player's hand. */
    public static final int MAX_HAND_SIZE = 5;
    /**
     * Maximum number of active units a player can have on the board at any time.
     */
    public static final int MAX_ACTIVE_UNITS = 5;
    /** Initial size of the player's deck at the start of the game. */
    public static final int INITIAL_DECK_SIZE = 40;

    /** Initial life points for each player at the start of the game. */
    public static final int INITIAL_LIFE_POINTS = 8000;

    /** Number of rows on the game board. */
    public static final int BOARD_ROWS = 7;
    /** Number of columns on the game board. */
    public static final int BOARD_COLS = 7;

    /** The prefix for error messages. */
    public static final String ERROR_PREFIX = "ERROR: ";

    private GameConstants() {
        // Private constructor to prevent instantiation of this utility class
    }
}
