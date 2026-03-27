package domain.graph;

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
    /** The modifier for bumpy surface conditions. */
    BUMPY(1.20),
    /** The modifier for icy surface conditions. */
    ICY(1.30);

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
}
