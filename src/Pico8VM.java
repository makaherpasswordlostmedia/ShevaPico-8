import java.util.*;

/**
 * Pico8VM - виртуальная машина PICO-8
 * Выполняет программы и предоставляет встроенные функции
 */
public class Pico8VM {
    
    private String program = "";
    private HashMap variables = new HashMap();
    private int playerX = 64;
    private int playerY = 64;
    private boolean[] buttonState = new boolean[6];
    
    // Встроенные функции
    private static final String BUILT_IN_FUNCTIONS = 
        "function _init() end\n" +
        "function _update() end\n" +
        "function _draw() end\n" +
        "function btn(n) return false end\n" +
        "function cls(c) end\n" +
        "function print(str, x, y, col) end\n" +
        "function rectfill(x1, y1, x2, y2, c) end\n" +
        "function rect(x1, y1, x2, y2, c) end\n" +
        "function circfill(x, y, r, c) end\n" +
        "function circ(x, y, r, c) end\n" +
        "function line(x1, y1, x2, y2, c) end\n" +
        "function pset(x, y, c) end\n" +
        "function pget(x, y) return 0 end\n" +
        "function spr(n, x, y, w, h, flip_x, flip_y) end\n" +
        "function map(cel_x, cel_y, sx, sy, w, h, layer) end\n" +
        "function mset(x, y, v) end\n" +
        "function mget(x, y) return 0 end\n" +
        "function sfx(n, ch, o, l) end\n" +
        "function music(n, f, m) end\n" +
        "function pal(c0, c1, p) end\n" +
        "function palt(c, t) end\n";
    
    public Pico8VM() {
        initVariables();
    }
    
    private void initVariables() {
        // Инициализация встроенных переменных
        variables.put("_version", "0.2.5");
        variables.put("_draw_state", new Integer(0));
    }
    
    public void loadProgram(String code) {
        this.program = code;
    }
    
    public void callInit() {
        executeFunction("_init");
    }
    
    public void callUpdate() {
        executeFunction("_update");
    }
    
    public void callDraw(GameState gameState) {
        executeFunction("_draw");
    }
    
    public void draw(GameState gameState) {
        // Простая демо-отрисовка
        gameState.clear(0);
        gameState.rectfill(gameState.getPlayerX() - 4, 
                          gameState.getPlayerY() - 4,
                          gameState.getPlayerX() + 4, 
                          gameState.getPlayerY() + 4, 3);
        
        drawText(gameState, "PICO-8 on J2ME", 20, 10, 7);
        drawText(gameState, "Use arrows to move", 20, 20, 7);
        drawText(gameState, "X: " + gameState.getPlayerX() + 
                          " Y: " + gameState.getPlayerY(), 20, 30, 7);
    }
    
    private void drawText(GameState gameState, String text, int x, int y, int color) {
        // Простой вывод текста (пиксельной сеткой)
        int charX = x;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            drawChar(gameState, c, charX, y, color);
            charX += 4;
        }
    }
    
    private void drawChar(GameState gameState, char c, int x, int y, int color) {
        // Рисуем символ как простые блоки для букв A-Z, 0-9
        switch (c) {
            case 'A':
                gameState.line(x, y+2, x, y+6, color);
                gameState.line(x+2, y, x+2, y+6, color);
                gameState.line(x, y+2, x+2, y+2, color);
                break;
            case 'B':
                gameState.line(x, y, x, y+6, color);
                gameState.line(x, y, x+2, y, color);
                gameState.line(x+2, y, x+2, y+3, color);
                gameState.line(x, y+3, x+2, y+3, color);
                gameState.line(x+2, y+3, x+2, y+6, color);
                gameState.line(x, y+6, x+2, y+6, color);
                break;
            case ' ':
                break;
            default:
                // Рисуем простой блок для неизвестных символов
                gameState.rectfill(x, y, x+2, y+6, color);
                break;
        }
    }
    
    public void executeFunction(String functionName) {
        // Простая имитация выполнения функций
        // В реальной реализации здесь был бы полноценный Lua интерпретатор
    }
    
    public void setButtonState(int button, boolean pressed) {
        if (button >= 0 && button < 6) {
            buttonState[button] = pressed;
        }
    }
    
    public boolean getButtonState(int button) {
        if (button >= 0 && button < 6) {
            return buttonState[button];
        }
        return false;
    }
    
    // Встроенная функция btn() - проверка кнопок
    public boolean btn(int n) {
        // 0 = LEFT, 1 = RIGHT, 2 = UP, 3 = DOWN, 4 = Z, 5 = X
        return getButtonState(n);
    }
    
    // Встроенная функция print()
    public void print(String text, int x, int y, int col, GameState gameState) {
        drawText(gameState, text, x, y, col);
    }
    
    // Встроенная функция cls() - очистка экрана
    public void cls(int color, GameState gameState) {
        gameState.clear(color);
    }
    
    // Встроенная функция rectfill()
    public void rectfill(int x1, int y1, int x2, int y2, int c, GameState gameState) {
        gameState.rectfill(x1, y1, x2, y2, c);
    }
    
    // Встроенная функция rect()
    public void rect(int x1, int y1, int x2, int y2, int c, GameState gameState) {
        gameState.rect(x1, y1, x2, y2, c);
    }
    
    // Встроенная функция circfill()
    public void circfill(int cx, int cy, int r, int c, GameState gameState) {
        gameState.circfill(cx, cy, r, c);
    }
    
    // Встроенная функция circ()
    public void circ(int cx, int cy, int r, int c, GameState gameState) {
        gameState.circ(cx, cy, r, c);
    }
    
    // Встроенная функция line()
    public void line(int x1, int y1, int x2, int y2, int c, GameState gameState) {
        gameState.line(x1, y1, x2, y2, c);
    }
    
    // Встроенная функция pset()
    public void pset(int x, int y, int c, GameState gameState) {
        gameState.setPixel(x, y, c);
    }
    
    // Встроенная функция pget()
    public int pget(int x, int y, GameState gameState) {
        return gameState.getPixel(x, y);
    }
    
    public String getProgram() {
        return program;
    }
    
    public HashMap getVariables() {
        return variables;
    }
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public Object getVariable(String name) {
        return variables.get(name);
    }
}
