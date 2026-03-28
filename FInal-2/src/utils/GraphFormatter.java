package utils;

import java.util.ArrayList;
import java.util.List;

import domain.graph.Lift;
import domain.graph.Piste;
import domain.graph.SkiGraph;
import exceptions.RoutingError;
import exceptions.RoutingException;

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
     * @throws RoutingException if the graph is null or contains no lifts
     * @return a string containing the information about each lift
     */
    public static String listLifts(SkiGraph graph) throws RoutingException {
        if (graph == null || graph.getAllLifts().isEmpty()) {
            throw new RoutingException(RoutingError.NO_GRAPH_LOADED.getMessage());
        }

        List<Lift> lifts = new ArrayList<>(graph.getAllLifts());
        lifts.sort((l1, l2) -> l1.getId().compareTo(l2.getId()));

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
     * @throws RoutingException if the graph is null or contains no pistes
     * @return a string containing the information about each piste
     */
    public static String listPistes(SkiGraph graph) throws RoutingException {
        if (graph == null || graph.getAllPistes().isEmpty()) {
            throw new RoutingException(RoutingError.NO_GRAPH_LOADED.getMessage());
        }

        List<Piste> pistes = new ArrayList<>(graph.getAllPistes());
        pistes.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));

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
