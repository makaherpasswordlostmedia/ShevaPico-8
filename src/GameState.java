import javax.microedition.lcdui.*;

/**
 * GameState - управление состоянием и буфером отрисовки PICO-8
 */
public class GameState {
    private static final int WIDTH = 128;
    private static final int HEIGHT = 128;
    
    // Палитра PICO-8 (16 цветов)
    private static final int[] PALETTE = {
        0x000000,  // 0: black
        0x1d2b53,  // 1: dark-blue
        0x7e2553,  // 2: dark-purple
        0x008751,  // 3: dark-green
        0xab5236,  // 4: brown
        0x5f574f,  // 5: dark-gray
        0xc2c3c7,  // 6: light-gray
        0xfff1e8,  // 7: white
        0xff004d,  // 8: red
        0xffa300,  // 9: orange
        0xffec27,  // 10: yellow
        0x00e436,  // 11: green
        0x29adff,  // 12: blue
        0x83769c,  // 13: indigo
        0xff77a8,  // 14: pink
        0xffccaa   // 15: peach
    };
    
    private int[] pixelBuffer = new int[WIDTH * HEIGHT];
    private int currentColor = 7; // white
    private int backgroundColor = 0; // black
    
    // Переменные игры
    private int playerX = 64;
    private int playerY = 64;
    private int playerSpeed = 2;
    
    public GameState() {
        clear(0);
    }
    
    public void update(boolean[] keys) {
        // Обновляем позицию игрока
        if (keys[0]) playerX -= playerSpeed;  // LEFT
        if (keys[1]) playerX += playerSpeed;  // RIGHT
        if (keys[2]) playerY -= playerSpeed;  // UP
        if (keys[3]) playerY += playerSpeed;  // DOWN
        
        // Границы
        if (playerX < 0) playerX = 0;
        if (playerX >= WIDTH) playerX = WIDTH - 1;
        if (playerY < 0) playerY = 0;
        if (playerY >= HEIGHT) playerY = HEIGHT - 1;
    }
    
    public void clear(int color) {
        int c = PALETTE[color & 0xF];
        for (int i = 0; i < pixelBuffer.length; i++) {
            pixelBuffer[i] = c;
        }
    }
    
    public void setPixel(int x, int y, int color) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            pixelBuffer[y * WIDTH + x] = PALETTE[color & 0xF];
        }
    }
    
    public int getPixel(int x, int y) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            return pixelBuffer[y * WIDTH + x];
        }
        return 0;
    }
    
    // Рисование прямоугольника
    public void rectfill(int x1, int y1, int x2, int y2, int color) {
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                setPixel(x, y, color);
            }
        }
    }
    
    // Рисование прямоугольника (контур)
    public void rect(int x1, int y1, int x2, int y2, int color) {
        // Верхняя и нижняя линии
        for (int x = x1; x <= x2; x++) {
            setPixel(x, y1, color);
            setPixel(x, y2, color);
        }
        // Левая и правая линии
        for (int y = y1; y <= y2; y++) {
            setPixel(x1, y, color);
            setPixel(x2, y, color);
        }
    }
    
    // Рисование окружности
    public void circfill(int cx, int cy, int r, int color) {
        for (int y = -r; y <= r; y++) {
            for (int x = -r; x <= r; x++) {
                if (x * x + y * y <= r * r) {
                    setPixel(cx + x, cy + y, color);
                }
            }
        }
    }
    
    // Рисование окружности (контур)
    public void circ(int cx, int cy, int r, int color) {
        int x = 0;
        int y = r;
        int d = 3 - 2 * r;
        
        while (x <= y) {
            setPixel(cx + x, cy + y, color);
            setPixel(cx - x, cy + y, color);
            setPixel(cx + x, cy - y, color);
            setPixel(cx - x, cy - y, color);
            setPixel(cx + y, cy + x, color);
            setPixel(cx - y, cy + x, color);
            setPixel(cx + y, cy - x, color);
            setPixel(cx - y, cy - x, color);
            
            if (d < 0) {
                d = d + 4 * x + 6;
            } else {
                d = d + 4 * (x - y) + 10;
                y--;
            }
            x++;
        }
    }
    
    // Рисование линии
    public void line(int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        
        while (true) {
            setPixel(x1, y1, color);
            if (x1 == x2 && y1 == y2) break;
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y1 += sy;
            }
        }
    }
    
    public int getPlayerX() {
        return playerX;
    }
    
    public int getPlayerY() {
        return playerY;
    }
    
    public Image getFrameBuffer() {
        // Конвертируем буфер пикселей в Image для отрисовки
        int[] rgb = new int[WIDTH * HEIGHT];
        for (int i = 0; i < pixelBuffer.length; i++) {
            rgb[i] = 0xFF000000 | pixelBuffer[i];
        }
        return Image.createRGBImage(rgb, WIDTH, HEIGHT, false);
    }
    
    public void renderToScreen(Graphics g, int offsetX, int offsetY, 
                              int scaleX, int scaleY) {
        // Отрисовка масштабированного буфера
        g.setColor(0x000000);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int color = pixelBuffer[y * WIDTH + x];
                g.setColor(color);
                g.fillRect(
                    offsetX + x * scaleX,
                    offsetY + y * scaleY,
                    scaleX,
                    scaleY
                );
            }
        }
        
        // Рисуем игрока
        int px = offsetX + playerX * scaleX;
        int py = offsetY + playerY * scaleY;
        g.setColor(0xFF3300);
        g.fillRect(px - 4 * scaleX, py - 4 * scaleY, 8 * scaleX, 8 * scaleY);
    }
    
    public void setCurrentColor(int color) {
        currentColor = color & 0xF;
    }
    
    public int getCurrentColor() {
        return currentColor;
    }
    
    public int[] getPixelBuffer() {
        return pixelBuffer;
    }
}
