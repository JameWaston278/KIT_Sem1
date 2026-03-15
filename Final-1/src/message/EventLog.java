package message;

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
    /** Template for logging when a unit no longer blocks. */
    NO_LONGER_BLOCKS("%s no longer blocks."),
    /** Template for logging when a unit moves to a new position. */
    MOVES_TO("%s moves to %s."),

    /**
     * Template for logging when a unit attacks another unit without showing
     * defender's stats.
     */
    ATTACK("%s (%d/%d) attacks %s on %s!"),
    /**
     * Template for logging when a unit attacks another unit while showing
     * defender's stats.
     */
    ATTACK_WITH_DEF_STATS("%s (%d/%d) attacks %s (%d/%d) on %s!"),

    /**
     * Template for logging when a unit is flipped.
     */
    FLIP("%s (%d/%d) was flipped on %s!"),
    /**
     * Template for logging when a unit is eliminated from the game.
     */
    ELIMINATED("%s was eliminated!"),
    /**
     * Template for logging when a unit takes damage, showing the amount of damage
     * taken.
     */
    DAMAGE("%s takes %d damage!"),

    // --- PLACE ---
    /** Template for logging when a unit is placed on the board. */
    PLACES("%s places %s on %s."),

    // --- COMBINE UNIT ---
    /**
     * Template for logging when two units join forces to combine into a stronger
     * unit.
     */
    JOIN_FORCES("%s and %s on %s join forces!"),
    /**
     * Template for logging when a combination of units is successful.
     */
    COMBINE_SUCCESS("Success!"),
    /**
     * Template for logging when a combination of units fails, resulting in one of
     * the units being eliminated.
     */
    COMBINE_FAIL("Union failed. %s was eliminated."),

    // --- BLOCK ---
    /** Template for logging when a unit blocks an attack. */
    BLOCKS("%s (%s) blocks!"),

    // --- STATUS/ WIN/ TURN ---
    /** Template for logging when a player's life points drop to zero. */
    LP_DROPPED_TO_ZERO("%s's life points dropped to 0!"),
    /** Template for logging when a player has no cards left in their deck. */
    DECK_EMPTY("%s has no cards left in the deck!"),
    /** Template for logging when a player wins the game. */
    WINS("%s wins!"),

    // --- YIELD ---
    /**
     * Template for logging when a player discards a card at the end of their turn,
     * showing the card that was discarded and the current hand size after
     * discarding.
     */
    DISCARDED("%s discarded %s (%d/%d)."),

    /**
     * Template for logging when the turn switches to the next player, showing the
     * name of the player whose turn it is now.
     */
    SWITCH_TURNS("It is %s's turn!");

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