/**
 * MapManager - управление тайлмапом PICO-8
 */
public class MapManager {
    
    private static final int MAP_WIDTH = 128;
    private static final int MAP_HEIGHT = 32;
    private static final int TILE_SIZE = 8;
    
    private byte[] mapData = new byte[MAP_WIDTH * MAP_HEIGHT];
    private SpriteManager spriteManager;
    
    public MapManager(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
        initializeDefaultMap();
    }
    
    /**
     * Инициализация карты по умолчанию
     */
    private void initializeDefaultMap() {
        // Простой паттерн: границы из спрайтов
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (y == 0 || y == MAP_HEIGHT - 1) {
                    mapData[y * MAP_WIDTH + x] = 1; // Верхняя/нижняя граница
                } else if (x == 0 || x == MAP_WIDTH - 1) {
                    mapData[y * MAP_WIDTH + x] = 1; // Левая/правая граница
                } else {
                    mapData[y * MAP_WIDTH + x] = 0; // Пустое место
                }
            }
        }
    }
    
    /**
     * Получить тайл по координатам
     */
    public byte getTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) {
            return 0;
        }
        return mapData[y * MAP_WIDTH + x];
    }
    
    /**
     * Установить тайл
     */
    public void setTile(int x, int y, byte spriteIndex) {
        if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
            mapData[y * MAP_WIDTH + x] = spriteIndex;
        }
    }
    
    /**
     * Отрисовать часть карты
     */
    public void drawMap(int cameraX, int cameraY, int screenWidth, int screenHeight,
                       GameState gameState) {
        
        // Вычисляем начальный тайл
        int startTileX = cameraX / TILE_SIZE;
        int startTileY = cameraY / TILE_SIZE;
        
        // Количество тайлов для отрисовки
        int tilesWidth = (screenWidth / TILE_SIZE) + 2;
        int tilesHeight = (screenHeight / TILE_SIZE) + 2;
        
        // Смещение в пиксельных единицах
        int offsetX = cameraX % TILE_SIZE;
        int offsetY = cameraY % TILE_SIZE;
        
        for (int ty = 0; ty < tilesHeight; ty++) {
            for (int tx = 0; tx < tilesWidth; tx++) {
                int mapX = startTileX + tx;
                int mapY = startTileY + ty;
                
                if (mapX >= 0 && mapX < MAP_WIDTH && 
                    mapY >= 0 && mapY < MAP_HEIGHT) {
                    
                    byte spriteIndex = getTile(mapX, mapY);
                    
                    int screenX = tx * TILE_SIZE - offsetX;
                    int screenY = ty * TILE_SIZE - offsetY;
                    
                    // Отрисовка спрайта на экран
                    spriteManager.drawSprite(spriteIndex & 0xFF, 
                                           screenX, screenY, gameState);
                }
            }
        }
    }
    
    /**
     * Отрисовать полную карту
     */
    public void drawMap(GameState gameState) {
        drawMap(0, 0, 128, 128, gameState);
    }
    
    /**
     * Получить высоту карты в тайлах
     */
    public int getMapHeightInTiles() {
        return MAP_HEIGHT;
    }
    
    /**
     * Получить ширину карты в тайлах
     */
    public int getMapWidthInTiles() {
        return MAP_WIDTH;
    }
    
    /**
     * Получить размер тайла
     */
    public int getTileSize() {
        return TILE_SIZE;
    }
    
    /**
     * Загрузить карту из данных
     */
    public void loadMapData(byte[] data) {
        int length = Math.min(data.length, mapData.length);
        System.arraycopy(data, 0, mapData, 0, length);
    }
    
    /**
     * Получить данные карты
     */
    public byte[] getMapData() {
        return mapData;
    }
    
    /**
     * Очистить карту
     */
    public void clear() {
        for (int i = 0; i < mapData.length; i++) {
            mapData[i] = 0;
        }
    }
    
    /**
     * Заполнить карту одним спрайтом
     */
    public void fill(byte spriteIndex) {
        for (int i = 0; i < mapData.length; i++) {
            mapData[i] = spriteIndex;
        }
    }
}
