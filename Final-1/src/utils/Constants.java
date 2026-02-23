package utils;

public final class Constants {

    // --- DISPLAY TEMPLATES & VALUES ---
    public static final String UNIT_DISPLAY_TEMPLATE = "%s (Team %s)%nATK: %s%nDEF: %s";
    public static final String KING_DISPLAY_TEMPLATE = "%s's Farmer King";
    public static final String FLIP_TEMPLATE = "%s %s was flipped on %s!%n";
    public static final String STATS_DISPLAY = "(%d/%d)";
    public static final String HIDDEN_VALUE = "???";

    // --- EVENT TEMPLATES ---
    public static final String ATTACK_EVENT = "%s %s attacks %s%s on %s!%n";
    public static final String MOVE_EVENT = "%s moves to %s.%n";
    public static final String TAKE_DAMAGE = "%s takes %d damage!%n";
    public static final String ELIMINATED_EVENT = "%s was eliminated!%n";
    public static final String COMBINE_EVENT = "%s and %s on %s join forces!%n";

    // --- MISC ---
    public static final String WHITESPACE = " ";
    public static final String EMPTY = "";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
