package domain.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import exceptions.ParseError;
import exceptions.ParseException;

/**
 * Class representing a ski graph, which consists of nodes (lifts and pistes)
 * and edges (connections between them).
 * The graph is used to model the ski area and validate its structure based on
 * specific rules.
 * 
 * @author udqch
 */
public class SkiGraph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<Node, List<Node>> adjacencyList = new HashMap<>();

    // public void printGraph() {
    // for (Node node : nodes.values()) {
    // System.out.println(node.getId());
    // List<Node> adjacentNodes = adjacencyList.get(node);
    // if (adjacentNodes != null) {
    // for (Node adjacent : adjacentNodes) {
    // System.out.println(" --> " + adjacent.getId());
    // }
    // }
    // }
    // }

    /**
     * Adds a node to the graph.
     * 
     * @param node the node to add
     * @throws ParseException if the node is invalid or already exists in the graph
     */
    public void addNode(Node node) throws ParseException {
        // Check if a node with the same ID already exists in the graph
        if (nodes.containsKey(node.getId())) {
            throw new ParseException(ParseError.EXISTING_NODE.getMessage(node.getId()));
        }

        // If the node is unique, add it to the graph and initialize its adjacency list
        nodes.put(node.getId(), node);
        adjacencyList.put(node, new ArrayList<>());
    }

    /**
     * Adds an edge between two nodes in the graph.
     * 
     * @param from the starting node of the edge
     * @param to   the ending node of the edge
     * @throws ParseException if the edge creates a self-loop or if either node is
     *                        invalid
     */
    public void addEdge(Node from, Node to) throws ParseException {
        // Check if the edge creates a self-loop (i.e., from and to are the same node)
        if (from.equals(to)) {
            throw new ParseException(ParseError.SELF_LOOP.getMessage());
        }

        // If the edge is valid, add it to the adjacency list of the 'from' node
        if (!nodes.containsKey(from.getId())) {
            addNode(from);
        }
        if (!nodes.containsKey(to.getId())) {
            addNode(to);
        }
        this.adjacencyList.get(from).add(to);
    }

    /**
     * Validates the structure of the ski graph based on specific rules:
     * 1. The graph must contain at least one piste and one talstation.
     * 2. If there is a symmetrical connection between two nodes (i.e., both nodes
     * have edges to each other), then the nodes must be of different types (one
     * must be a lift and the other must be a piste).
     * 3. The graph must be fully connected, meaning there must be a path between
     * any two nodes in the graph.
     * 
     * @throws ParseException if any of the validation rules are violated
     */
    public void validate() throws ParseException {
        if (nodes.isEmpty()) {
            throw new ParseException(ParseError.EMPTY_GRAPH.getMessage());
        }

        checkMinimumRequirements();
        checkSymmetricalRules();
        checkConnectivity();
    }

    // Check for minimum requirements: at least one piste and one talstation
    private void checkMinimumRequirements() throws ParseException {
        boolean hasPiste = false;
        boolean hasTalstation = false;

        for (Node node : nodes.values()) {
            if (node instanceof Piste) {
                hasPiste = true;
            } else if (node instanceof Lift lift) {
                if (lift.isTalstation()) {
                    hasTalstation = true;
                }
            }
        }

        if (!hasPiste) {
            throw new ParseException(ParseError.NO_PISTE.getMessage());
        }
        if (!hasTalstation) {
            throw new ParseException(ParseError.NO_TALSTATION.getMessage());
        }
    }

    // Check for symmetrical connections and ensure that connected nodes are of
    // different types
    private void checkSymmetricalRules() throws ParseException {
        for (Node u : nodes.values()) {
            for (Node v : getAdjacencyNodes(u)) {
                // Check if there is a reverse edge from v to u
                if (getAdjacencyNodes(v).contains(u)) {
                    // If both nodes are of the same type (both Piste or both Lift), this is an
                    // invalid connection
                    if (u.getClass().equals(v.getClass())) {
                        throw new ParseException(ParseError.INVALID_CONNECTION.getMessage(u.getId(), v.getId()));
                    }
                }
            }
        }
    }

    // Perform a breadth-first search (BFS) to check if the graph is fully connected
    private void checkConnectivity() throws ParseException {
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        Node startNode = nodes.values().iterator().next(); // Start BFS from an arbitrary node
        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Node neighbor : getAdjacencyNodes(current)) {
                if (visited.add(neighbor)) { // If neighbor has not been visited
                    queue.add(neighbor);
                }
            }

            for (Node potentialParent : nodes.values()) {
                if (getAdjacencyNodes(potentialParent).contains(current)) {
                    if (visited.add(potentialParent)) { // If potential parent has not been visited
                        queue.add(potentialParent);
                    }
                }
            }
        }

        if (visited.size() != nodes.size()) {
            throw new ParseException(ParseError.NOT_CONNECTED.getMessage());
        }
    }

    // --- GETTERS ---

    /**
     * Returns an unmodifiable list of adjacent nodes for the given node.
     *
     * @param node the node for which to retrieve adjacent nodes
     * @return an unmodifiable list of adjacent nodes
     */
    public List<Node> getAdjacencyNodes(Node node) {
        List<Node> adjacentNodes = adjacencyList.getOrDefault(node, Collections.emptyList());
        return Collections.unmodifiableList(adjacentNodes);
    }

    /**
     * Returns an unmodifiable list of all lift nodes in the graph.
     *
     * @return an unmodifiable list of all lift nodes
     */
    public List<Lift> getAllLifts() {
        List<Lift> lifts = new ArrayList<>();
        for (Node node : nodes.values()) {
            if (node instanceof Lift lift) {
                lifts.add(lift);
            }
        }
        return Collections.unmodifiableList(lifts);
    }

    /**
     * Returns an unmodifiable list of all piste nodes in the graph.
     *
     * @return an unmodifiable list of all piste nodes
     */
    public List<Piste> getAllPistes() {
        List<Piste> pistes = new ArrayList<>();
        for (Node node : nodes.values()) {
            if (node instanceof Piste piste) {
                pistes.add(piste);
            }
        }
        return Collections.unmodifiableList(pistes);
    }

    /**
     * Returns the node with the specified ID, or null if no such node exists in
     * the graph.
     *
     * @param id the ID of the node to retrieve
     * @return the node with the specified ID, or null if no such node exists
     */
    public Node getNodeById(String id) {
        return nodes.get(id);
    }
}
