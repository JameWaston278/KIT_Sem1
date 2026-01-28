/**
 * Centralizes all system output messages, error notifications, and validation
 * warnings. Uses format specifiers (e.g., %s, %d) for dynamic content.
 *
 * @author udqch
 */
public enum SystemMessage {

    // --- GENERAL ERRORS ---
    /** Prefix for error messages. */
    ERROR_PREFIX("ERROR: "),
    /** Message when a command is not recognized. */
    UNREGISTERED_COMMAND("command not found"),

    // --- VALIDATION ERRORS ---
    /** Error for non-positive integer IDs. */
    INVALID_ID("invalid ID, must be a positive integer."),
    /** Error for names containing spaces. */
    INVALID_NAME("invalid name, must not contain spaces or newlines."),
    /** Error for list names containing non-letter characters. */
    INVALID_LIST_NAME("invalid list name, must contain only letters."),
    /** Error for invalid priority strings. */
    INVALID_PRIORITY("invalid priority. Expected: HI, MD, or LO."),
    /** Error for invalid date formats. */
    INVALID_DEADLINE("invalid date format. Expected: YYYY-MM-DD."),
    /** Error for invalid tag formats. */
    INVALID_TAG_NAME("invalid tag, must contain only letters and numbers."),
    /** Error for insufficient command arguments. */
    INVALID_ARGUMENTS("invalid number of arguments. Expected at least %d."),
    /** Generic error for invalid parameters. */
    INVALID_PARAMETER("invalid parameter %s."),

    // --- LIST ERRORS ---
    /** Error when creating a duplicate list. */
    LIST_EXISTS("list \"%s\" already exists."),
    /** Error when accessing a non-existent list. */
    LIST_NOT_FOUND("list \"%s\" does not exist."),
    /** Info message when a list has no tasks. */
    LIST_EMPTY("list \"%s\" is empty."),

    // --- TAG ERRORS ---
    /** Error when adding a duplicate tag. */
    TAG_EXISTS("tag \"%s\" already exists."),

    // --- TASK ERRORS ---
    /** Error when accessing a non-existent task ID. */
    TASK_NOT_FOUND("task with ID \"%d\" does not exist."),
    /** Error when attempting modification on a deleted task. */
    TASK_DELETED("this task is deleted."),
    /** Error when attempting restore an active task. */
    TASK_ACTIVE("this task is still active."),
    /** Error when adding a task to a list it already belongs to. */
    TASK_IN_LIST("task already in list."),
    /** Message prefix for duplicate task findings. */
    TASK_DUPLICATE("Found %d duplicates: %s"),
    /** Message for assign two times. */
    ASSIGNED("%s is already subtask of %s"),
    /** Error when assigning a task to itself. */
    ASSIGN_ITSELF("cannot assign task to itself."),
    /** Error when creating a circular dependency. */
    ASSIGN_WITH_CYCLE("cannot assign task as subtask of its descendant."),

    // --- SUCCESS MESSAGES ---
    /** Success message for adding a task. */
    SUCCESS_ADD_TASK("added %d: %s"),
    /** Success message for creating a list. */
    SUCCESS_CREATE_LIST("added %s"),
    /** Success message for tagging. */
    SUCCESS_TAG("tagged %s with %s"),
    /** Success message for assignment. */
    SUCCESS_ASSIGN("assigned %s to %s"),
    /** Success message for toggling state. */
    SUCCESS_TOGGLE("toggled %s and %d subtasks"),
    /** Success message for deletion. */
    SUCCESS_DELETE("deleted %s and %d subtasks"),
    /** Success message for restoration. */
    SUCCESS_RESTORE("restored %s and %d subtasks"),
    /** Success message for changing priority or deadline. */
    SUCCESS_CHANGE_VALUES("changed %s to %s");

    private final String message;

    SystemMessage(String message) {
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
     * 
     * @return The message string.
     */
    @Override
    public String toString() {
        return message;
    }
}