import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class SimpleMIDlet extends MIDlet {
  public void startApp() {
    Form form = new Form("PICO-8");
    form.append("Hello J2ME!");
    Display.getDisplay(this).setCurrent(form);
  }
  public void pauseApp() {}
  public void destroyApp(boolean u) {}
