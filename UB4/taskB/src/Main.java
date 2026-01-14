/**
 * The entry point of the Mau-Mau game application.
 * Initializes the game logic and the user interface.
 *
 * @author udqch
 * @version 1.0
 */
public class Main {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Main() {
    }

    /**
     * The main method that starts the application.
     *
     * @param args Command line arguments (not used).
     * @throws GameException If a critical error occurs during initialization.
     */
    public static void main(String[] args) throws GameException {
        MauMauGame game = new MauMauGame();
        MauMauUI gameUI = new MauMauUI(game);
        gameUI.run();
    }
}