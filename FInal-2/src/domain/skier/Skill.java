package domain.skier;

/**
 * Enum representing the skill levels of skiers.
 * Each skill level has an associated modifier that can be used to
 * calculate the time taken to ski down a slope.
 * 
 * @author udqch
 */
public enum Skill {

    /** The modifier for beginner skiers. */
    BEGINNER(1.35),
    /** The modifier for intermediate skiers. */
    INTERMEDIATE(1.10),
    /** The modifier for expert skiers. */
    EXPERT(0.90);

    private final double modifier;

    Skill(double modifier) {
        this.modifier = modifier;
    }

    /**
     * Returns the modifier associated with the skill level.
     * 
     * @return the modifier for this skill level
     */
    public double getModifier() {
        return this.modifier;
    }
}
