public class Pico8Emulator {
    private static Pico8Canvas gameCanvas;
    public Pico8Emulator() {}
    public static void main(String[] args) {
        System.out.println("PICO-8 J2ME Emulator v1.0");
    }
    public void startApp() {
        if (gameCanvas == null) gameCanvas = new Pico8Canvas(this);
        gameCanvas.start();
    }
    public void pauseApp() { if (gameCanvas != null) gameCanvas.pause(); }
    public void destroyApp(boolean u) { if (gameCanvas != null) gameCanvas.stop(); System.exit(0); }
}
