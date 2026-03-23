package domain.skier;

import java.util.EnumMap;
import java.util.Map;

import domain.graph.Difficulty;
import domain.graph.Surface;

/****
 * The SkierProfile class represents the profile of a skier, including their
 * skill
 * level, skiing goals, and preferences for different types of ski routes. It
 * allows setting and retrieving the skier's skill, goal, and preferences for
 * route difficulty and surface type.
 *
 * @author udqch
 */
public class SkierProfile {
    private Skill skill;
    private Goal goal;
    private final Map<Difficulty, Preference> difficultyPrefs;
    private final Map<Surface, Preference> surfacePrefs;

    /** Constructs a new SkierProfile with default preferences. */
    public SkierProfile() {
        this.difficultyPrefs = new EnumMap<>(Difficulty.class);
        this.surfacePrefs = new EnumMap<>(Surface.class);
    }

    // --- SETTERS ---

    /**
     * Sets the skill level of the skier.
     *
     * @param targetSkill the skill level to set
     */
    public void setSkill(Skill targetSkill) {
        this.skill = targetSkill;
    }

    /**
     * Sets the skiing goal of the skier.
     *
     * @param targetGoal the goal to set
     */
    public void setGoal(Goal targetGoal) {
        this.goal = targetGoal;
    }

    /**
     * Sets the preference for a specific difficulty level.
     *
     * @param difficulty the difficulty level to set the preference for
     * @param preference the preference to set
     */
    public void setDifficultyPreference(Difficulty difficulty, Preference preference) {
        this.difficultyPrefs.put(difficulty, preference);
    }

    /**
     * Sets the preference for a specific surface type.
     *
     * @param surface    the surface type to set the preference for
     * @param preference the preference to set
     */
    public void setSurfacePreference(Surface surface, Preference preference) {
        this.surfacePrefs.put(surface, preference);
    }

    /**
     * Resets all preferences for difficulty and surface to neutral.
     */
    public void resetPreferences() {
        for (Difficulty d : Difficulty.values()) {
            this.difficultyPrefs.put(d, Preference.NEUTRAL);
        }
        for (Surface s : Surface.values()) {
            this.surfacePrefs.put(s, Preference.NEUTRAL);
        }
    }

    // --- GETTERS ---

    /**
     * Returns the skill level of the skier.
     *
     * @return the skill level
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * Returns the skiing goal of the skier.
     *
     * @return the goal
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Returns the preference for a specific difficulty level.
     *
     * @param difficulty the difficulty level to get the preference for
     * @return the preference for the specified difficulty
     */
    public Preference getPreference(Difficulty difficulty) {
        return this.difficultyPrefs.get(difficulty);
    }

    /**
     * Returns the preference for a specific surface type.
     *
     * @param surface the surface type to get the preference for
     * @return the preference for the specified surface
     */
    public Preference getPreference(Surface surface) {
        return this.surfacePrefs.get(surface);
    }
}
