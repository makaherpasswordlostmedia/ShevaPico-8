/**
 * SpriteManager - управление спрайтами PICO-8
 */
public class SpriteManager {
    
    private static final int SPRITE_SIZE = 8;
    private static final int SPRITES_PER_ROW = 16;
    private static final int TOTAL_SPRITES = 256;
    
    private byte[] spriteSheet = new byte[TOTAL_SPRITES * SPRITE_SIZE * SPRITE_SIZE];
    private byte[] spriteFlags = new byte[TOTAL_SPRITES];
    
    public SpriteManager() {
        initializeDefaultSprites();
    }
    
    /**
     * Инициализация спрайтов по умолчанию
     */
    private void initializeDefaultSprites() {
        // Простые тестовые спрайты
        // Спрайт 0: красный квадрат
        fillSprite(0, 15);
        
        // Спрайт 1: белый круг
        drawCircleSprite(1, 7);
    }
    
    /**
     * Заполнить спрайт цветом
     */
    private void fillSprite(int spriteIndex, int color) {
        int offset = spriteIndex * SPRITE_SIZE * SPRITE_SIZE;
        for (int i = 0; i < SPRITE_SIZE * SPRITE_SIZE; i++) {
            spriteSheet[offset + i] = (byte)(color & 0xF);
        }
    }
    
    /**
     * Нарисовать круг в спрайт
     */
    private void drawCircleSprite(int spriteIndex, int color) {
        int offset = spriteIndex * SPRITE_SIZE * SPRITE_SIZE;
        int center = SPRITE_SIZE / 2;
        int radius = SPRITE_SIZE / 2 - 1;
        
        for (int y = 0; y < SPRITE_SIZE; y++) {
            for (int x = 0; x < SPRITE_SIZE; x++) {
                int dx = x - center;
                int dy = y - center;
                if (dx * dx + dy * dy <= radius * radius) {
                    spriteSheet[offset + y * SPRITE_SIZE + x] = 
                        (byte)(color & 0xF);
                }
            }
        }
    }
    
    /**
     * Отрисовать спрайт на игровое поле
     */
    public void drawSprite(int spriteIndex, int x, int y, 
                          GameState gameState, boolean flipX, boolean flipY) {
        if (spriteIndex < 0 || spriteIndex >= TOTAL_SPRITES) {
            return;
        }
        
        int offset = spriteIndex * SPRITE_SIZE * SPRITE_SIZE;
        
        for (int sy = 0; sy < SPRITE_SIZE; sy++) {
            for (int sx = 0; sx < SPRITE_SIZE; sx++) {
                byte pixelData = spriteSheet[offset + sy * SPRITE_SIZE + sx];
                int color = pixelData & 0xF;
                
                // Обработка отражения
                int drawX = flipX ? (x + SPRITE_SIZE - 1 - sx) : (x + sx);
                int drawY = flipY ? (y + SPRITE_SIZE - 1 - sy) : (y + sy);
                
                if (color != 0) { // 0 = прозрачный цвет
                    gameState.setPixel(drawX, drawY, color);
                }
            }
        }
    }
    
    /**
     * Отрисовать спрайт без отражения
     */
    public void drawSprite(int spriteIndex, int x, int y, GameState gameState) {
        drawSprite(spriteIndex, x, y, gameState, false, false);
    }
    
    /**
     * Установить спрайт из данных
     */
    public void setSprite(int spriteIndex, byte[] spriteData) {
        if (spriteIndex < 0 || spriteIndex >= TOTAL_SPRITES) {
            return;
        }
        
        int offset = spriteIndex * SPRITE_SIZE * SPRITE_SIZE;
        int length = Math.min(spriteData.length, SPRITE_SIZE * SPRITE_SIZE);
        
        System.arraycopy(spriteData, 0, spriteSheet, offset, length);
    }
    
    /**
     * Получить спрайт данные
     */
    public byte[] getSprite(int spriteIndex) {
        if (spriteIndex < 0 || spriteIndex >= TOTAL_SPRITES) {
            return new byte[SPRITE_SIZE * SPRITE_SIZE];
        }
        
        byte[] sprite = new byte[SPRITE_SIZE * SPRITE_SIZE];
        int offset = spriteIndex * SPRITE_SIZE * SPRITE_SIZE;
        
        System.arraycopy(spriteSheet, offset, sprite, 0, 
                        SPRITE_SIZE * SPRITE_SIZE);
        
        return sprite;
    }
    
    /**
     * Установить флаги спрайта
     */
    public void setSpriteFlag(int spriteIndex, int flag, boolean value) {
        if (spriteIndex >= 0 && spriteIndex < TOTAL_SPRITES) {
            if (value) {
                spriteFlags[spriteIndex] |= (1 << flag);
            } else {
                spriteFlags[spriteIndex] &= ~(1 << flag);
            }
        }
    }
    
    /**
     * Получить флаг спрайта
     */
    public boolean getSpriteFlag(int spriteIndex, int flag) {
        if (spriteIndex >= 0 && spriteIndex < TOTAL_SPRITES) {
            return (spriteFlags[spriteIndex] & (1 << flag)) != 0;
        }
        return false;
    }
    
    /**
     * Получить ширину спрайта
     */
    public int getSpriteWidth() {
        return SPRITE_SIZE;
    }
    
    /**
     * Получить высоту спрайта
     */
    public int getSpriteHeight() {
        return SPRITE_SIZE;
    }
}
