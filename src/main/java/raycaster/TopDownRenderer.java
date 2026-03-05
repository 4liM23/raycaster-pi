package raycaster;

// temporary debugger class until I get everything working
public class TopDownRenderer {
    private static final int TILE_SIZE = 20;

    public void render(MapGrid map, Player p, PixelBuffer buf) {
        buf.clear();
        // map
        for (int r = 0; r < map.getHeight(); r++) {
            for (int c = 0; c < map.getWidth(); c++) {
                if (map.isWall(c, r))
                    buf.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE, PixelBuffer.WHITE);
            }
        }

        // player
        buf.fillCircle((int) (p.getX() * TILE_SIZE), (int) (p.getY() * TILE_SIZE), (int) (p.getRadius() * TILE_SIZE),
                PixelBuffer.BLUE);

        // direction line
        buf.drawLine((int) (p.getX() * TILE_SIZE), (int) (p.getY() * TILE_SIZE), 12, p.getAngle(),
                PixelBuffer.RED);
    }

}
