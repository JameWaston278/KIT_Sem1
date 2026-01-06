public class Postamt {
    public static void main(String[] args) {
        PostOffice postamt = new PostOffice();
        PostOfficeUI ui = new PostOfficeUI(postamt);
        ui.run();
    }
}