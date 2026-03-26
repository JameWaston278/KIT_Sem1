package domain.graph;

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
}
