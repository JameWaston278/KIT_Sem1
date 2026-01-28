package kit.edu.kastel;

/**
 * Enumeration of all supported commands in the application.
 * Each constant represents a specific user action mapped to a command string.
 *
 * @author udqch
 */
public enum CommandType {
    /** Terminates the application. */
    QUIT("quit"),

    /** Adds a new task to the system. */
    ADD("add"),

    /** Creates a new task list. */
    ADD_LIST("add-list"),

    /** Adds a tag to a task or a list. */
    TAG("tag"),

    /** Assigns a subtask to a parent or a task to a list. */
    ASSIGN("assign"),

    /** Toggles the completion status of a task (and its subtasks). */
    TOGGLE("toggle"),

    /** Updates the deadline of a task. */
    CHANGE_DATE("change-date"),

    /** Updates the priority of a task. */
    CHANGE_PRIORITY("change-priority"),

    /** Deletes a task and its subtasks. */
    DELETE("delete"),

    /** Restores a previously deleted task. */
    RESTORE("restore"),

    /** Displays the structure of a specific task or all roots. */
    SHOW("show"),

    /** Lists all tasks that are not yet done. */
    TODO("todo"),

    /** Displays tasks contained in a specific list. */
    LIST("list"),

    /** Displays all tasks associated with a specific tag. */
    TAGGED_WITH("tagged-with"),

    /** Searches for tasks containing a specific name. */
    FIND("find"),

    /** Lists tasks with deadlines within the next 7 days from a date. */
    UPCOMING("upcoming"),

    /** Lists tasks with deadlines before or on a specific date. */
    BEFORE("before"),

    /** Lists tasks with deadlines between two specific dates. */
    BETWEEN("between"),

    /** Identifies and lists duplicate tasks based on name and deadline. */
    DUPLICATES("duplicates");

    private final String commandName;

    /**
     * Constructs a CommandType with its string representation.
     * 
     * @param commandName The keyword used in the console.
     */
    CommandType(String commandName) {
        this.commandName = commandName;
    }

    /**
     * Gets the string representation of the command.
     * 
     * @return The command name.
     */
    public String getName() {
        return commandName;
    }
}