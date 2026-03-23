package domain.skier;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import domain.graph.Piste;

/**
 * Enum representing different goals that a skier can have when choosing a route
 * through the ski area. Each goal has a method to calculate the utility of a
 * list of pistes based on the specific criteria of that goal.
 * 
 * @author udqch
 */
public enum Goal {
    /**
     * Goal representing the total altitude drop of the pistes.
     */
    ALTITUDE {
        @Override
        public int calculateUtility(List<Piste> pistes) {
            int sum = 0;
            for (Piste piste : pistes) {
                sum += piste.getElevationDrop();
            }
            return sum;
        }
    },
    /**
     * Goal representing the total distance of the pistes.
     */
    DISTANCE {
        @Override
        public int calculateUtility(List<Piste> pistes) {
            int sum = 0;
            for (Piste piste : pistes) {
                sum += piste.getLength();
            }
            return sum;
        }
    },
    /**
     * Goal representing the number of pistes.
     */
    NUMBER {
        @Override
        public int calculateUtility(List<Piste> pistes) {
            return pistes.size();
        }
    },
    /**
     * Goal representing the uniqueness of pistes.
     */
    UNIQUE {
        @Override
        public int calculateUtility(List<Piste> pistes) {
            Set<Piste> uniquePistes = new HashSet<>(pistes);
            return uniquePistes.size();
        }
    };

    /**
     * Abstract method to calculate the utility of a list of pistes based on the
     * specific goal. Each goal will have its own implementation of how to
     * calculate the utility.
     *
     * @param pistes the list of pistes for which to calculate the utility
     * @return the calculated utility value for the given list of pistes
     */
    public abstract int calculateUtility(List<Piste> pistes);

    /**
     * Converts a string to a Goal enum value. The conversion is case-insensitive
     * and ignores leading/trailing whitespace.
     *
     * @param text the string to convert
     * @return an Optional containing the corresponding Goal, or an empty
     *         Optional if the input is invalid
     */
    public static Optional<Goal> fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedText = text.trim().toUpperCase();
        for (Goal goal : Goal.values()) {
            if (goal.name().equals(normalizedText)) {
                return Optional.of(goal);
            }
        }
        return Optional.empty();
    }
}
