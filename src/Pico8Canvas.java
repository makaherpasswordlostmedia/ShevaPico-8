import java.util.*;
public class Pico8Canvas implements Runnable {
    private Pico8Emulator midlet;
    private GameState gameState;
    private Pico8VM vm;
    private Thread gameThread;
    private boolean running = false;
    public Pico8Canvas(Pico8Emulator midlet) {
        this.midlet = midlet;
        this.gameState = new GameState();
        this.vm = new Pico8VM();
    }
    public void start() { running = true; gameThread = new Thread(this); gameThread.start(); }
    public void pause() { running = false; }
    public void stop() { running = false; }
    public void run() { while (running) { try { Thread.sleep(33); } catch (Exception e) {} } }
}
