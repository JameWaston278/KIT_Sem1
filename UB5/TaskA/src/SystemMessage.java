/**
 * Centralizes all system output messages, error notifications, and validation
 * warnings.
 *
 * @author udqch
 */
public enum SystemMessage {

    ERROR_PREFIX("Error, "),

    LIST_EXISTS("List \"%s\" already exists."),
    TAG_EXISTS("Tag \"%s\" already exists."),
    TASK_NOT_FOUND("Task with ID \"%d\" does not exist."),
    LIST_NOT_FOUND("List \"%s\" does not exist."),

    private final String message;

    private SystemMessage(String message) {
        this.message = message;
    }

    /**
     * Formats the message pattern with given arguments.
     *
     * @param args Arguments for placeholders (%d, %s).
     * @return The formatted string.
     */
    public String format(Object... args) {
        return String.format(message, args);
    }

    /**
     * Returns the raw message string.
     */
    @Override
    public String toString() {
        return message;
    }
}