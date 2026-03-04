package raycaster;

// temporary debugger class until I get everything working
public class TopDownRenderer {
    private static final int TILE_SIZE = 20;

    public void render(MapGrid map, Player p, PixelBuffer buf) {
        int[][] grid = map.getMap();
        // map
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                buf.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE, buf.WHITE);
            }
        }

        // player
        buf.fillCircle((int) (p.getX() * TILE_SIZE), (int) (p.getY() * TILE_SIZE), (int) (p.getRadius() * TILE_SIZE),
                buf.BLUE);

        // direction line
        buf.drawLine((int) (p.getX() * TILE_SIZE), (int) (p.getY() * TILE_SIZE), 7, p.getAngle(),
                buf.RED);
    }
}
