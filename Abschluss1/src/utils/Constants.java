package utils;

public final class Constants {

    private Constants() {
        // Private constructor to prevent instantiation
    }

    // --- GAME CONSTANTS ---
    public static final int INIT_LP = 8000;
    public static final int INIT_CARDS_IN_HAND = 4;

    // --- KING's CONSTANTS ---
    public static final String KING_QUALIFIER = "Farmer";
    public static final String KING_ROLE = "King";
    public static final int KING_ATK = 0;
    public static final int KING_DEF = 0;

    // --- DISPLAY TEMPLATES & VALUES ---
    public static final String UNIT_DISPLAY_TEMPLATE = "%s (Team %s)%nATK: %s%nDEF: %s";
    public static final String KING_DISPLAY_TEMPLATE = "%s's Farmer King";
    public static final String FLIP_TEMPLATE = "%s %s was flipped on %s!%n";
    public static final String STATS_DISPLAY = "(%d/%d)";
    public static final String HIDDEN_VALUE = "???";

    // --- EVENT TEMPLATES ---
    public static final String ATTACK_EVENT = "%s %s attacks %s%s on %s!%n";

    // --- MISC ---
    public static final String WHITESPACE = " ";
    public static final String EMPTY = "";
}
