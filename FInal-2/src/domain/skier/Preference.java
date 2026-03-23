package domain.skier;

/**
 * Enum representing the preference of a skier for difficulty levels or surface
 * of ski piste. The preference can be one of three values: LIKE, DISLIKE, or
 * NEUTRAL.
 *
 * @author udqch
 */
public enum Preference {
    /**
     * Preference indicating that the skier likes a certain difficulty level or
     * surface.
     */
    LIKE,
    /**
     * Preference indicating that the skier dislikes a certain difficulty level or
     * surface.
     */
    DISLIKE,
    /**
     * Preference indicating that the skier has no strong feelings (neither like
     * nor dislike) towards a certain difficulty level or surface.
     */
    NEUTRAL;
}
