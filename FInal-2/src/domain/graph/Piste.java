package domain.graph;

/**
 * Class representing a piste in a ski area.
 * Each piste has a difficulty level, surface type, length, and elevation drop.
 * 
 * @author udqch
 */
public class Piste extends Node {

    private final Difficulty difficulty;
    private final Surface surface;
    private final double length;
    private final int elevationDrop;

    /**
     * Constructor to initialize the piste with the specified parameters.
     *
     * @param id            the unique identifier for the piste
     * @param difficulty    the difficulty level of the piste (BLUE, RED, BLACK)
     * @param surface       the surface type of the piste (REGULAR, ICY, BUMPY)
     * @param length        the length of the piste in meters
     * @param elevationDrop the elevation drop of the piste in meters
     */
    public Piste(String id, Difficulty difficulty, Surface surface, double length, int elevationDrop) {
        super(id);
        this.difficulty = difficulty;
        this.surface = surface;
        this.length = length;
        this.elevationDrop = elevationDrop;
    }

    // --- GETTERS ---
    /**
     * Returns the difficulty level of the piste.
     *
     * @return the difficulty level of the piste
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the surface type of the piste.
     *
     * @return the surface type of the piste
     */
    public Surface getSurface() {
        return surface;
    }

    /**
     * Returns the length of the piste in meters.
     *
     * @return the length of the piste in meters
     */
    public double getLength() {
        return length;
    }

    /**
     * Returns the elevation drop of the piste in meters.
     *
     * @return the elevation drop of the piste in meters
     */
    public int getElevationDrop() {
        return elevationDrop;
    }
}
