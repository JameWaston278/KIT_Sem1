package utils;

/**
 * The DisplayFormat enum defines various string templates used for displaying
 * information about units, stats, and hidden values in the game.
 * 
 * @author udqch
 */
public enum DisplayFormat {

    UNIT_INFO("%s (Team %s)%nATK: %s%nDEF: %s"),
    KING_INFO("%s's Farmer King"),
    STATS("(%d/%d)"),
    HIDDEN_SYMBOL("???"),
    COORDINATES("[%d, %d]");

    private final String template;

    /**
     * Constructor for the DisplayFormat enum, which initializes each format with a
     * specific template string.
     * 
     * @param template The string template associated with this display format,
     *                 which can include placeholders for dynamic values (e.g., %s,
     *                 %d) to be filled in when formatting the display string.
     */
    DisplayFormat(String template) {
        this.template = template;
    }

    /**
     * Get the raw template string for this display format. This can be used for
     * manual formatting or for reference.
     * 
     * @return The raw template string associated with this display format
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Formats the display string using the provided arguments. This method uses
     * String.format to replace placeholders in the template with actual values.
     * 
     * @param args The arguments to be formatted into the template
     * @return The formatted string ready for display
     */
    public String format(Object... args) {
        return String.format(this.template, args);
    }
}