public class Main {

    private Main() {
    }

    public static void main(String[] args) throws GameException {
        MauMauGame game = new MauMauGame();
        MauMauUI gameUI = new MauMauUI(game);
        gameUI.run();
    }
}
