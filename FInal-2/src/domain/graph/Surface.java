package domain.graph;

import java.util.Optional;

/**
 * Enum representing the surface conditions of ski slopes.
 * Each surface condition has an associated modifier that can be used to
 * calculate the time taken to ski down a slope.
 * 
 * @author udqch
 */
public enum Surface {

    /** The modifier for regular surface conditions. */
    REGULAR(1.00),
    /** The modifier for icy surface conditions. */
    ICY(1.20),
    /** The modifier for bumpy surface conditions. */
    BUMPY(1.30);

    private final double modifier;

    Surface(double modifier) {
        this.modifier = modifier;
    }

    /**
     * Returns the modifier associated with the surface condition.
     * 
     * @return the modifier for this surface condition
     */
    public double getModifier() {
        return this.modifier;
    }

    /**
     * Converts a string to a Surface enum value.
     * The conversion is case-insensitive and ignores leading/trailing whitespace.
     *
     * @param text the string to convert
     * @return an Optional containing the corresponding Surface, or an empty
     *         Optional if the input is invalid
     */
    public static Optional<Surface> fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Optional.empty();
        }

        // Iterate through the Surface values to find a match
        String normalizedText = text.trim().toUpperCase();
        for (Surface surface : Surface.values()) {
            if (surface.name().equals(normalizedText)) {
                return Optional.of(surface);
            }
        }

        return Optional.empty();
    }
}
