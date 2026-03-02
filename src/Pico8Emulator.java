import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Pico8Emulator extends MIDlet {
  private static Pico8Canvas gameCanvas;
  private Display display;
  
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
}
