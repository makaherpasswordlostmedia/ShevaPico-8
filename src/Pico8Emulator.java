import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;

/**
 * PICO-8 Emulator for J2ME
 * Базовая реализация эмулятора PICO-8 для J2ME телефонов
 */
public class Pico8Emulator extends MIDlet {
    private Display display;
    private Pico8Canvas gameCanvas;
    
    public Pico8Emulator() {
        super();
    }
    
    public void startApp() {
        if (display == null) {
            display = Display.getDisplay(this);
        }
        
        if (gameCanvas == null) {
            gameCanvas = new Pico8Canvas(this);
        }
        
        display.setCurrent(gameCanvas);
        gameCanvas.start();
    }
    
    public void pauseApp() {
        if (gameCanvas != null) {
            gameCanvas.pause();
        }
    }
    
    public void destroyApp(boolean unconditional) {
        if (gameCanvas != null) {
            gameCanvas.stop();
        }
        notifyDestroyed();
    }
    
    public void exitApp() {
        destroyApp(true);
    }
}
