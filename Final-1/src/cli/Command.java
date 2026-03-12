package cli;

/**
 * The Command enum represents the various commands that can be entered by the
 * user in the command-line interface (CLI) of the game. Each enum constant
 * corresponds to a specific command that the user can use to interact with the
 * game.
 * The enum also provides a method to convert a string input into a
 * corresponding Command enum value, allowing for case-insensitive command
 * recognition.
 * 
 * @author udqch
 */
public enum Command {
    /** The "select" command. */
    SELECT("select"),
    /** The "board" command. */
    BOARD("board"),
    /** The "move" command. */
    MOVE("move"),
    /** The "flip" command. */
    FLIP("flip"),
    /** The "block" command. */
    BLOCK("block"),
    /** The "show" command. */
    SHOW("show"),
    /** The "hand" command. */
    HAND("hand"),
    /** The "place" command. */
    PLACE("place"),
    /** The "state" command. */
    STATE("state"),
    /** The "yield" command. */
    YIELD("yield"),
    /** The "quit" command. */
    QUIT("quit");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Converts a string input into a corresponding Command enum value. The
     * comparison is case-insensitive.
     *
     * @param text The input string to convert.
     * @return The corresponding Command enum value, or null if the input does not
     *         match any command.
     */
    public static Command fromString(String text) {
        for (Command cmd : Command.values()) {
            if (cmd.keyword.equalsIgnoreCase(text)) {
                return cmd;
            }
        }
        return null; // Return null if the user enters an invalid command
    }
}