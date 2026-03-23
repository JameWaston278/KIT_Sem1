package domain.graph;

import java.util.Optional;

/**
 * Enum representing the types of ski lifts.
 * 
 * @author udqch
 */
public enum LiftType {

    /** The gondola lift type. */
    GONDOLA,
    /** The chairlift type. */
    CHAIRLIFT;

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * Converts a string to a LiftType enum value.
     * The conversion is case-insensitive and ignores leading/trailing whitespace.
     *
     * @param text the string to convert
     * @return an Optional containing the corresponding LiftType, or an empty
     *         Optional if the input is invalid
     */
    public static Optional<LiftType> fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Optional.empty();
        }

        // Iterate through the LiftType values to find a match
        String normalizedText = text.trim().toUpperCase();
        for (LiftType type : LiftType.values()) {
            if (type.name().equals(normalizedText)) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
