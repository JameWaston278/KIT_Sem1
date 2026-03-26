package domain.graph;

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
}
