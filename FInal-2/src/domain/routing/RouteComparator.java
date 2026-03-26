package domain.routing;

import java.util.Comparator;

import domain.graph.Node;
import domain.graph.Piste;
import domain.skier.Preference;
import domain.skier.SkierProfile;

/**
 * The RouteComparator class implements the Comparator interface to compare two
 * routes based on their score, skier preferences, and lexicographical order
 * of their node sequences.
 *
 * @author udqch
 */
public class RouteComparator implements Comparator<Route> {
    private final SkierProfile skier;

    /**
     * Constructs a new RouteComparator with the given skier profile.
     *
     * @param skier the skier profile used to evaluate route preferences
     */
    public RouteComparator(SkierProfile skier) {
        this.skier = skier;
    }

    @Override
    public int compare(Route r1, Route r2) {

        // First compare by goal score (higher is better)
        int scoreCompare = Integer.compare(r1.getScore(), r2.getScore());
        if (scoreCompare != 0) {
            return scoreCompare;
        }

        // If scores are equal, compare by skier preferences (higher preference score is
        // better)
        int prefCompare = Integer.compare(calculatePreferenceScore(r1), calculatePreferenceScore(r2));
        if (prefCompare != 0) {
            return prefCompare;
        }

        // If both score and preference score are equal, compare by lexicographical
        // order of node sequences (lower is better)
        return getRouteString(r2).compareTo(getRouteString(r1)); // Lexicographical order of route strings
    }

    private int calculatePreferenceScore(Route route) {
        int score = 0;
        for (Node node : route.getRoute()) {
            if (node instanceof Piste piste) {
                // Increase score for liked attributes, decrease for disliked attributes
                if (skier.getPreference(piste.getDifficulty()).equals(Preference.LIKE)) {
                    score++;
                }
                if (skier.getPreference(piste.getDifficulty()).equals(Preference.DISLIKE)) {
                    score--;
                }
                if (skier.getPreference(piste.getSurface()).equals(Preference.LIKE)) {
                    score++;
                }
                if (skier.getPreference(piste.getSurface()).equals(Preference.DISLIKE)) {
                    score--;
                }
            }
        }
        return score;
    }

    private String getRouteString(Route route) {
        StringBuilder sb = new StringBuilder();
        for (Node node : route.getRoute()) {
            sb.append(node.getId()).append(" ");
        }
        return sb.toString().trim();
    }
}