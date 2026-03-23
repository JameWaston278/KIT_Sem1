package domain.graph;

import java.util.Optional;

/**
 * Enum representing the difficulty levels of ski slopes.
 * Each difficulty level has an associated modifier that can be used to
 * calculate the time taken to ski down a slope.
 * 
 * @author udqch
 */
public enum Difficulty {

    /** The modifier for blue slopes. */
    BLUE(1.00),
    /** The modifier for red slopes. */
    RED(1.15),
    /** The modifier for black slopes. */
    BLACK(1.35);

    private final double modifier;

    Difficulty(double modifier) {
        this.modifier = modifier;
    }

    /**
     * Returns the modifier associated with the difficulty level.
     * 
     * @return the modifier for this difficulty level
     */
    public double getModifier() {
        return this.modifier;
    }

    /**
     * Converts a string to a Difficulty enum value.
     * The conversion is case-insensitive and ignores leading/trailing whitespace.
     *
     * @param text the string to convert
     * @return an Optional containing the corresponding Difficulty, or an empty
     *         Optional if the input is invalid
     */
    public static Optional<Difficulty> fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Optional.empty();
        }

        // Iterate through the Difficulty values to find a match
        String normalizedText = text.trim().toUpperCase();
        for (Difficulty diff : Difficulty.values()) {
            if (diff.name().equals(normalizedText)) {
                return Optional.of(diff);
            }
        }

        return Optional.empty();
    }
}
