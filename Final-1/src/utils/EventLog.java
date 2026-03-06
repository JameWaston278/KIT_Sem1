package utils;

/**
 * The EventLog enum defines various string templates for logging different
 * types of events that occur during the game, such as attacks, flips, moves,
 * damage taken, eliminations, and combinations. Each event type has a specific
 * template that can be formatted with relevant information when the event
 * occurs.
 * 
 * @author udqch
 */
public enum EventLog {

    /** Template for logging attack events. */
    ATTACK("%s %s attacks %s%s on %s!%n"),
    /** Template for logging flip events. */
    FLIP("%s %s was flipped on %s!%n"),
    /** Template for logging move events. */
    MOVE("%s moves to %s.%n"),
    /** Template for logging damage taken events. */
    DAMAGE_TAKEN("%s takes %d damage!%n"),
    /** Template for logging elimination events. */
    ELIMINATION("%s was eliminated!%n"),
    /** Template for logging combination events. */
    COMBINATION("%s and %s on %s join forces!%n");

    private final String template;

    /**
     * Constructor for the EventLog enum, which initializes each event type with a
     * specific template string.
     * 
     * @param template The string template associated with this event type, which
     *                 can include placeholders for dynamic values (e.g., %s, %d) to
     *                 be filled in when formatting the event log message.
     */
    EventLog(String template) {
        this.template = template;
    }

    /**
     * Get the raw template string for this event log type. This can be used for
     * manual formatting or for reference.
     * 
     * @return The raw template string associated with this event log type.
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Formats the event log message using the provided arguments. This method uses
     * String.format to replace placeholders in the template with actual values.
     * 
     * @param args The arguments to be formatted into the template, which should
     *             correspond to the placeholders defined in the template string for
     *             this event type.
     * @return The formatted event log message ready for display or logging.
     */
    public String format(Object... args) {
        return String.format(this.template, args);
    }

    /**
     * Prints the formatted event log message to the console using the provided
     * arguments.
     * 
     * @param args The arguments to be formatted into the template, which should
     *             correspond to the placeholders defined in the template string for
     *             this event type.
     */
    public void print(Object... args) {
        System.out.printf(this.template, args);
    }
}