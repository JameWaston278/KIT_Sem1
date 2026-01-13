public class Main {
    public static void main(String[] args) throws ErrorException {
        MauMauGame mmg = new MauMauGame();
        try {
            mmg.start(7101825);
            mmg.showGame();
            mmg.discard(1, "10L");
            mmg.show(1);
            mmg.discard(2, "DL");
            mmg.discard(3, "8E");
            mmg.pick(3);
            mmg.showGame();
            mmg.quit();
        } catch (ErrorException e) {
            System.out.println("Error, " + e.getMessage());
        }
    }
}
