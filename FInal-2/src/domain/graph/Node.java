package domain.graph;

/**
 * Abstract class representing a node in a graph.
 * Each node has a unique identifier (id).
 * 
 * @author udqch
 */
public abstract class Node {

    private final String id;

    /**
     * Constructor to initialize the node with a unique identifier.
     *
     * @param id the unique identifier for the node
     */
    public Node(String id) {
        this.id = id;
    }

    /**
     * Getter for the node's unique identifier.
     *
     * @return the unique identifier of the node
     */
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        // Check if the object is null or of a different class
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        Node other = (Node) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}