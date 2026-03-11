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

    // --- MOVE & DUEL ---
    NO_LONGER_BLOCKS("%s no longer blocks."),
    MOVES_TO("%s moves to %s."),

    ATTACK("%s (%d/%d) attacks %s on %s!"),
    ATTACK_WITH_DEF_STATS("%s (%d/%d) attacks %s (%d/%d) on %s!"),

    FLIP("%s (%d/%d) was flipped on %s!"), //
    ELIMINATED("%s was eliminated!"), //
    DAMAGE("%s takes %d damage!"), //

    // --- PLACE ---
    PLACES("%s places %s on %s."), //

    // --- COMBINE UNIT ---
    JOIN_FORCES("%s and %s on %s join forces!"), //
    COMBINE_SUCCESS("Success!"), //
    COMBINE_FAIL("Union failed. %s was eliminated."), //

    // --- BLOCK ---
    BLOCKS("%s (%s) blocks!"), //

    // --- STATUS/ WIN/ TURN ---
    LP_DROPPED_TO_ZERO("%s's life points dropped to 0!"), //
    DECK_EMPTY("%s has no cards left in the deck!"), //
    WINS("%s wins!"), //
    TURN_START("It is %s's turn!"), //

    // --- YIELD ---
    DISCARDED("%s discarded %s (%d/%d)."); //

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
}