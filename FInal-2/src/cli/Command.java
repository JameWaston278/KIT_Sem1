package cli;

/**
 * Enum representing the different commands that can be used in the ski resort
 * management system. Each command has an associated keyword that can be used to
 * identify it when parsing user input.
 * 
 * @author udqch
 */
public enum Command {
    /** Command to quit the application. */
    QUIT("quit"),
    /** Command to load a ski graph from a file. */
    LOAD("load"),
    /** Command to list all available ski routes. */
    LIST("list"),
    /** Command to set parameters for ski routes. */
    SET("set"),
    /** Command to like a ski route. */
    LIKE("like"),
    /** Command to dislike a ski route. */
    DISLIKE("dislike"),
    /** Command to reset all preferences of skier to neutral. */
    RESET("reset"),
    /** Command to plan a ski route. */
    PLAN("plan"),
    /** Command to show the current ski route. */
    ABORT("abort"),
    /** Command to show the next step in the current ski route. */
    NEXT("next"),
    /** Command to take the current ski route. */
    TAKE("take"),
    /** Command to show alternative ski routes. */
    ALTERNATIVE("alternative"),
    /** Command to show the current ski route. */
    SHOW("show");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword associated with this command.
     * 
     * @return the keyword for this command
     */
    public String getKeyword() {
        return keyword;
    }
}
