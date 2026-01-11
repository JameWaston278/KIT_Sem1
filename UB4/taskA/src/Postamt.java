/**
 * Main entry point for the Post Office application.
 * Initializes the backend (PostOffice) and the frontend (PostOfficeUI).
 */
public class Postamt {

    /**
     * The main method to start the program.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        PostOffice postOffice = new PostOffice();
        PostOfficeUI ui = new PostOfficeUI(postOffice);

        // Start the application loop
        ui.run();
    }
}