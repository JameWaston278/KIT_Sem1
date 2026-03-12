package cli;

/**
 * The CliMessages class is a utility class that contains constant string
 * messages used in the command-line interface (CLI) of the game. It provides a
 * centralized location for all user-facing messages, including welcome
 * messages, error messages, and other prompts.
 * 
 * @author udqch
 */
public final class CliMessages {

    /** Welcome messages. */
    public static final String WELCOME_MSG = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";

    /** Error prefix messages. */
    public static final String ERROR_PREFIX = "ERROR: ";

    // --- STATIC ERROR MESSAGES ---

    /** Unknown command error message. */
    public static final String ERR_UNKNOWN_COMMAND = ERROR_PREFIX + "Unknown command.";
    /** Invalid command format error message. */
    public static final String ERR_INVALID_ARGS = ERROR_PREFIX + "Invalid command arguments.";
    /** No selection error message. */
    public static final String ERR_NO_SELECTION = ERROR_PREFIX + "No field selected. Use 'select <field>' first.";

    // --- DYNAMIC ERROR MESSAGES ---
    public static final String ERR_AI_CRASH = "AI encountered an error: %s";

    private CliMessages() {
        // Private constructor to prevent instantiation
    }

    /**
     * Formats an error message with the given detail message.
     *
     * @param detailMessage The specific details of the error to include in the
     *                      message.
     * @param args          Optional arguments to format into the detail message.
     * @return A formatted error message string.
     */
    public static String formatError(String detailMessage, Object... args) {
        return ERROR_PREFIX + String.format(detailMessage, args);
    }
}