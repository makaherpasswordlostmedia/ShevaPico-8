import javax.microedition.lcdui.*;
import java.io.*;

/**
 * Pico8Canvas - основной canvas для отрисовки и игрового цикла
 */
public class Pico8Canvas extends Canvas implements Runnable {
    private static final int PICO8_WIDTH = 128;
    private static final int PICO8_HEIGHT = 128;
    
    private Pico8Emulator midlet;
    private GameState gameState;
    private Pico8VM vm;
    private Graphics graphics;
    private Thread gameThread;
    private boolean running = false;
    
    // Масштабирование для отображения на разных экранах
    private int scaleX;
    private int scaleY;
    private int offsetX;
    private int offsetY;
    
    // Input
    private boolean[] keys = new boolean[256];
    private int lastKeyCode = 0;
    
    // FPS
    private long lastFrameTime = 0;
    private int frameCount = 0;
    private int currentFps = 0;
    
    public Pico8Canvas(Pico8Emulator midlet) {
        this.midlet = midlet;
        this.gameState = new GameState();
        this.vm = new Pico8VM();
        
        setFullScreenMode(true);
        calculateScale();
    }
    
    private void calculateScale() {
        int canvasWidth = getWidth();
        int canvasHeight = getHeight();
        
        scaleX = canvasWidth / PICO8_WIDTH;
        scaleY = canvasHeight / PICO8_HEIGHT;
        
        int scale = Math.min(scaleX, scaleY);
        if (scale < 1) scale = 1;
        
        scaleX = scaleY = scale;
        offsetX = (canvasWidth - PICO8_WIDTH * scaleX) / 2;
        offsetY = (canvasHeight - PICO8_HEIGHT * scaleY) / 2;
    }
    
    public void start() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void pause() {
        running = false;
    }
    
    public void stop() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
        }
    }
    
    public void run() {
        // Инициализация виртуальной машины с примером кода
        initializeVM();
        
        while (running) {
            long startTime = System.currentTimeMillis();
            
            // Обновление игры
            update();
            
            // Перерисовка
            repaint();
            
            // Контроль FPS (30 FPS)
            long elapsed = System.currentTimeMillis() - startTime;
            long delay = 33 - elapsed; // 1000/30 = 33ms
            
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
            }
            
            // Подсчет FPS
            frameCount++;
            if (System.currentTimeMillis() - lastFrameTime >= 1000) {
                currentFps = frameCount;
                frameCount = 0;
                lastFrameTime = System.currentTimeMillis();
            }
        }
    }
    
    private void initializeVM() {
        // Загружаем встроенную программу (простая демо)
        String demoProgram = 
            "function _init()\n" +
            "  x = 64\n" +
            "  y = 64\n" +
            "  speed = 2\n" +
            "end\n" +
            "\n" +
            "function _update()\n" +
            "  if btn(0) then x = x - speed end\n" +
            "  if btn(1) then x = x + speed end\n" +
            "  if btn(2) then y = y - speed end\n" +
            "  if btn(3) then y = y + speed end\n" +
            "end\n" +
            "\n" +
            "function _draw()\n" +
            "  cls(0)\n" +
            "  rectfill(x-4, y-4, x+4, y+4, 3)\n" +
            "  print('PICO-8 on J2ME', 20, 10, 7)\n" +
            "  print('Use arrows', 30, 20, 7)\n" +
            "end\n";
        
        vm.loadProgram(demoProgram);
        vm.callInit();
    }
    
    private void update() {
        vm.callUpdate();
        gameState.update(keys);
    }
    
    protected void paint(Graphics g) {
        // Черный фон
        g.setColor(0, 0, 0);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Отрисовка игры
        Image frameBuffer = gameState.getFrameBuffer();
        if (frameBuffer != null) {
            g.drawImage(frameBuffer, offsetX, offsetY, Graphics.TOP | Graphics.LEFT);
        }
        
        // Отрисовка через VM
        vm.draw(gameState);
        gameState.renderToScreen(g, offsetX, offsetY, scaleX, scaleY);
        
        // Отрисовка FPS (опционально)
        g.setColor(255, 255, 255);
        g.drawString("FPS: " + currentFps, 5, 5, Graphics.TOP | Graphics.LEFT);
    }
    
    protected void keyPressed(int keyCode) {
        lastKeyCode = keyCode;
        updateKeys(keyCode, true);
    }
    
    protected void keyReleased(int keyCode) {
        updateKeys(keyCode, false);
    }
    
    private void updateKeys(int keyCode, boolean pressed) {
        // Маппинг кнопок на PICO-8 управление
        // 0 = LEFT, 1 = RIGHT, 2 = UP, 3 = DOWN, 4 = Z, 5 = X
        switch (keyCode) {
            case KEY_LEFT:
            case '4':
                keys[0] = pressed;
                break;
            case KEY_RIGHT:
            case '6':
                keys[1] = pressed;
                break;
            case KEY_UP:
            case '2':
                keys[2] = pressed;
                break;
            case KEY_DOWN:
            case '8':
                keys[3] = pressed;
                break;
            case '5':
            case KEY_SOFT_LEFT:
                keys[4] = pressed;
                break;
            case '0':
            case KEY_SOFT_RIGHT:
                keys[5] = pressed;
                break;
        }
    }
    
    protected void sizeChanged(int w, int h) {
        calculateScale();
    }
}
