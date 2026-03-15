package message;

/**
 * The CliMessages class is a utility class that contains constant string
 * messages used in the command-line interface (CLI) of the game. It provides a
 * centralized location for all user-facing messages, including welcome
 * messages, error messages, and other prompts.
 * 
 * @author udqch
 */
public enum CliMessages {

    /** Welcome messages. */
    WELCOME_MSG(
            "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit."),

    /** Unknown command error message. */
    UNKNOWN_COMMAND("Unknown command."),
    /** Invalid command format error message. */
    INVALID_FORMAT("Invalid command format."),
    /** Invalid command format error message. */
    INVALID_ARGS("Invalid command arguments."),
    /** No selection error message. */
    NO_SELECTION("No field selected. Use 'select <field>' first."),

    /** Invalid move error message with details. */
    AI_CRASH("AI encountered an error: %s");

    private final String text;

    CliMessages(String text) {
        this.text = text;
    }

    /**
     * Returns the message text.
     * 
     * @return The message text.
     */
    public String get() {
        return text;
    }

    /**
     * Formats an error message with the given detail message.
     *
     * @param detailMessage The specific details of the error to include in the
     *                      message.
     * @param args          Optional arguments to format into the detail message.
     * @return A formatted error message string.
     */
    public static String format(String detailMessage, Object... args) {
        return String.format(detailMessage, args);
    }
}