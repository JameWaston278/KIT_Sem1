/**
 * Centralizes all system output messages, error notifications, and validation
 * warnings.
 *
 * @author udqch
 */
public enum SystemMessage {

    ERROR_PREFIX("Error: "),
    UNREGISTERED_COMMAND("command not found"),

    INVALID_ID("invalid ID: must be a positive integer"),
    INVALID_NAME("invalid name: must not contain spaces or newlines"),
    INVALID_LIST_NAME("invalid list name: must contain only letters"),
    INVALID_PRIORITY("invalid priority. Expected: HI, MD, or LO"),
    INVALID_DEADLINE("invalid date format. Expected: YYYY-MM-DD"),
    INVALID_TAG_NAME("invalid tag: must contain only letters and numbers"),
    INVALID_ARGUMENTS("invalid number of arguments. Expected at least %d"),
    INVALID_PARAMETER("invalid parameter %s"),

    LIST_EXISTS("list \"%s\" already exists"),
    LIST_NOT_FOUND("list \"%s\" does not exist"),
    LIST_EMPTY("list \"%s\" is empty"),

    TAG_EXISTS("tag \"%s\" already exists"),

    TASK_NOT_FOUND("task with ID \"%d\" does not exist"),
    TASK_DELETED("this task is deleted"),
    TASK_IN_LIST("task already in list"),
    TASK_DUPLICATE("Found %d duplicates: "),
    ASSIGN_ITSELF("cannot assign task to itself"),
    ASSIGN_WITH_CYCLE("cannot assign task as subtask of its descendant"),

    SUCCESS_ADD_TASK("added %d: %s"),
    SUCCESS_CREATE_LIST("added %s"),
    SUCCESS_TAG("tagged %s with %s"),
    SUCCESS_ASSIGN("assigned %s to %s"),
    SUCCESS_TOGGLE("toggled %s and %d subtasks"),
    SUCCESS_DELETE("deleted %s and %d subtasks"),
    SUCCESS_RESTORE("restored %s and %d subtasks"),
    SUCCESS_CHANGE_VALUES("changed %s to %s"),

    NO_MATCHED("no tasks found"),
    NO_DUPLICATE("no duplicate found");

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
     */
    @Override
    public String toString() {
        return message;
    }
}