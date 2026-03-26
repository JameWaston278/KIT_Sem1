package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import domain.graph.Lift;
import domain.graph.Node;
import domain.graph.Piste;
import domain.graph.SkiGraph;

/**
 * The CommandList class provides methods for listing various elements of the
 * ski graph, such as lifts and pistes. It formats the output according to the
 * specified requirements and handles cases where there are no elements to list.
 *
 * @author udqch
 */
public final class GraphFormatter {
    private static final String LIFTS_FORMAT = "%s %s %s %s %d %d";
    private static final String TALSTATION_TYPE = "TRANSIT";
    private static final String PISTES_FORMAT = "%s %s %s %d %d";

    private GraphFormatter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Lists all lifts in the ski graph.
     *
     * @param graph the ski graph containing the lifts to list
     * @return a string containing the information about each lift
     */
    public static String listLifts(SkiGraph graph) {
        if (graph == null || graph.getAllLifts().isEmpty()) {
            return "";
        }

        List<Lift> lifts = new ArrayList<>(graph.getAllLifts());
        lifts.sort(Comparator.comparing(Node::getId));

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lifts.size(); i++) {
            Lift lift = lifts.get(i);

            String line = String.format(LIFTS_FORMAT,
                    lift.getId(), lift.getType().name(), lift.getStartTime().toString(), lift.getEndTime().toString(),
                    lift.getRideTime(), lift.getQueueTime());
            if (lift.isTalstation()) {
                line += " " + TALSTATION_TYPE;
            }

            result.append(line);
            if (i < lifts.size() - 1) {
                result.append(System.lineSeparator());
            }
        }

        return result.toString();
    }

    /**
     * Lists all pistes in the ski graph.
     *
     * @param graph the ski graph containing the pistes to list
     * @return a string containing the information about each piste
     */
    public static String listPistes(SkiGraph graph) {
        if (graph == null || graph.getAllPistes().isEmpty()) {
            return "";
        }

        List<Piste> pistes = new ArrayList<>(graph.getAllPistes());
        pistes.sort(Comparator.comparing(Node::getId));

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pistes.size(); i++) {
            Piste piste = pistes.get(i);
            String line = String.format(PISTES_FORMAT,
                    piste.getId(), piste.getDifficulty().name(), piste.getSurface().name(),
                    (int) piste.getLength(), (int) piste.getElevationDrop());

            result.append(line);
            if (i < pistes.size() - 1) {
                result.append(System.lineSeparator());
            }
        }

        return result.toString();
    }

}
