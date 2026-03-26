package domain.skier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import domain.graph.Node;
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
        public int calculate(List<Piste> pistes) {
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
        public int calculate(List<Piste> pistes) {
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
        public int calculate(List<Piste> pistes) {
            return pistes.size();
        }
    },
    /**
     * Goal representing the uniqueness of pistes.
     */
    UNIQUE {
        @Override
        public int calculate(List<Piste> pistes) {
            Set<Piste> uniquePistes = new HashSet<>(pistes);
            return uniquePistes.size();
        }
    };

    /**
     * Calculates the utility of a list of nodes based on the specific goal.
     *
     * @param rawPath the list of nodes for which to calculate the utility
     * @return the calculated utility value for the given list of nodes
     */
    public int calculateUtility(List<Node> rawPath) {
        List<Piste> pistes = new ArrayList<>();
        for (Node node : rawPath) {
            if (node instanceof Piste piste) {
                pistes.add(piste);
            }
        }
        return calculate(pistes);
    }

    /**
     * Abstract method to calculate the utility of a list of pistes based on the
     * specific criteria of the goal. Each enum constant must implement this method
     * to provide its own calculation logic.
     *
     * @param pistes the list of pistes for which to calculate the utility
     * @return the calculated utility value for the given list of pistes
     */
    protected abstract int calculate(List<Piste> pistes);
}
