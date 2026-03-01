import java.io.*;

/**
 * CartridgeLoader - загрузка и парсинг PICO-8 картриджей
 */
public class CartridgeLoader {
    
    // Структура PICO-8 картриджа
    private static final byte[] P8_MAGIC = {0x70, 0x69, 0x63, 0x6F}; // "pico"
    private static final int HEADER_SIZE = 0x8;
    
    private String luaCode;
    private byte[] spriteData;
    private byte[] mapData;
    private byte[] musicData;
    private byte[] soundData;
    private int[] palette;
    
    public CartridgeLoader() {
        this.luaCode = "";
        this.spriteData = new byte[0x1000];      // 16x16 спрайтов * 8x8 пиксели
        this.mapData = new byte[0x1000];         // 128x32 тайлов
        this.musicData = new byte[0x100];        // 4 трека музыки
        this.soundData = new byte[0x800];        // 64 звука
        this.palette = new int[16];              // 16 цветов
    }
    
    /**
     * Загрузить картридж из файла
     */
    public boolean loadFromFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            return loadFromStream(fis);
        } catch (FileNotFoundException e) {
            System.err.println("Картридж не найден: " + filePath);
            return false;
        } catch (IOException e) {
            System.err.println("Ошибка чтения картриджа: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Загрузить картридж из потока
     */
    private boolean loadFromStream(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        
        // Проверка магии
        byte[] magic = new byte[4];
        dis.readFully(magic);
        
        if (!isMagic(magic)) {
            // Вероятно, текстовый P8 файл
            return loadFromText(is);
        }
        
        // Чтение заголовка (версия, размер)
        byte version = dis.readByte();
        byte cartDataFormat = dis.readByte();
        dis.readShort(); // reserved
        
        // Разбор секций картриджа
        while (true) {
            try {
                byte sectionId = dis.readByte();
                if (sectionId == -1) break; // EOF
                
                int size = dis.readInt();
                byte[] data = new byte[size];
                dis.readFully(data);
                
                parseSection(sectionId, data);
            } catch (EOFException e) {
                break;
            }
        }
        
        dis.close();
        return true;
    }
    
    /**
     * Загрузить из текстового P8 файла
     */
    private boolean loadFromText(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(is)
        );
        
        String line;
        String section = "";
        StringBuilder codeBuilder = new StringBuilder();
        
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("__")) {
                section = line;
                continue;
            }
            
            if (section.equals("__lua__")) {
                codeBuilder.append(line).append("\n");
            }
        }
        
        this.luaCode = codeBuilder.toString();
        reader.close();
        return true;
    }
    
    /**
     * Парсинг секций картриджа
     */
    private void parseSection(byte sectionId, byte[] data) {
        switch (sectionId) {
            case 0: // Код
                this.luaCode = new String(data);
                break;
            case 1: // Спрайты
                this.spriteData = data;
                break;
            case 2: // Тайлмап
                this.mapData = data;
                break;
            case 3: // Звуки
                this.soundData = data;
                break;
            case 4: // Музыка
                this.musicData = data;
                break;
            default:
                System.out.println("Неизвестная секция: " + sectionId);
        }
    }
    
    /**
     * Проверка магического числа P8
     */
    private boolean isMagic(byte[] magic) {
        if (magic.length != 4) return false;
        return magic[0] == 0x70 && 
               magic[1] == 0x69 && 
               magic[2] == 0x63 && 
               magic[3] == 0x6F;
    }
    
    // Getters
    
    public String getLuaCode() {
        return luaCode;
    }
    
    public byte[] getSpriteData() {
        return spriteData;
    }
    
    public byte[] getMapData() {
        return mapData;
    }
    
    public byte[] getMusicData() {
        return musicData;
    }
    
    public byte[] getSoundData() {
        return soundData;
    }
    
    public int[] getPalette() {
        return palette;
    }
    
    /**
     * Получить спрайт (8x8 пиксели)
     */
    public int[] getSprite(int spriteIndex) {
        int[] sprite = new int[64];
        int baseOffset = spriteIndex * 64;
        
        for (int i = 0; i < 64 && baseOffset + i < spriteData.length; i++) {
            sprite[i] = spriteData[baseOffset + i] & 0xFF;
        }
        
        return sprite;
    }
    
    /**
     * Установить спрайт
     */
    public void setSprite(int spriteIndex, int[] sprite) {
        int baseOffset = spriteIndex * 64;
        
        for (int i = 0; i < 64 && baseOffset + i < spriteData.length; i++) {
            spriteData[baseOffset + i] = (byte)(sprite[i] & 0xFF);
        }
    }
    
    /**
     * Получить тайл из тайлмапа
     */
    public byte getTile(int x, int y) {
        if (x < 0 || x >= 128 || y < 0 || y >= 32) {
            return 0;
        }
        return mapData[y * 128 + x];
    }
    
    /**
     * Установить тайл в тайлмапе
     */
    public void setTile(int x, int y, byte tileIndex) {
        if (x >= 0 && x < 128 && y >= 0 && y < 32) {
            mapData[y * 128 + x] = tileIndex;
        }
    }
    
    /**
     * Информация о картридже
     */
    public String getInfo() {
        return "PICO-8 Cartridge\n" +
               "Code size: " + luaCode.length() + " bytes\n" +
               "Sprites: " + (spriteData.length / 64) + "\n" +
               "Map size: 128x32 tiles\n" +
               "Sounds: " + (soundData.length / 32) + "\n" +
               "Music: " + (musicData.length / 4) + " tracks";
    }
}
