package ai;

/**
 * A record that holds an action and its associated score.
 *
 * @param <T>    the type of the action
 * @param choice the action chosen
 * @param score  the score associated with the action
 * 
 * @author udqch
 */
public record ScoredActions<T>(T choice, int score) {

}
