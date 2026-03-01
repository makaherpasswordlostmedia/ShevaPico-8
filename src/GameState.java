public class GameState {
    private int[] pixelBuffer = new int[128 * 128];
    private int[] PALETTE = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    private int playerX = 64, playerY = 64;
    
    public GameState() { }
    public void update(boolean[] keys) { }
    public void clear(int c) { }
    public void setPixel(int x, int y, int c) { }
    public void line(int x1, int y1, int x2, int y2, int c) { }
    public void rect(int x1, int y1, int x2, int y2, int c) { }
    public void circfill(int cx, int cy, int r, int c) { }
    public void circ(int cx, int cy, int r, int c) { }
    public int getPixel(int x, int y) { return 0; }
    public void rectfill(int x1, int y1, int x2, int y2, int c) { }
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
}
