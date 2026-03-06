package raycaster;

public class Raycaster {

    private final int screenWidth;
    private final int screenHeight;

    private final double horizontalFov;
    private final double projectionPlaneDistance;

    private final Texture wallTexture;

    public Raycaster(int screenWidth, int screenHeight, double horizontalFov) {
        this.wallTexture = new Texture("assets/textures/walls/test.png");
        // debug
        System.out
                .println(wallTexture.getWidth() + "   " + wallTexture.getHeight() + "   " + wallTexture.getPixel(0, 0));
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.horizontalFov = horizontalFov * (Math.PI / 180);
        this.projectionPlaneDistance = (screenWidth / 2) / Math.tan(this.horizontalFov / 2);
    }

    public void renderWalls(Player player, MapGrid map, PixelBuffer buffer) {

        buffer.clear();

        for (int col = 0; col < screenWidth; col++) {

            double rayAngle = computeRayAngle(player, col);

            RayHit hit = castRay(player, map, rayAngle);

            double perp = computePerpendicularDistance(player, rayAngle, hit);

            int sliceHeight = computeWallSliceHeight(map.getWallHeight(), perp);

            int top = computeWallTop(sliceHeight, player, map.getWallHeight());

            int bottom = computeWallBottom(top, sliceHeight);

            int wallX = (int) (wallTexture.getWidth() * computeWallX(player, rayAngle, hit));
            drawTexturedVerticalSlice(buffer, wallTexture, col, top, bottom, wallX);
            // drawVerticalSlice(buffer, col, top, bottom, color);

        }
    }

    private double computeRayAngle(Player player, int screenX) {
        return player.getAngle() - horizontalFov / 2 + (screenX + 0.5) * horizontalFov / screenWidth;
    }

    private RayHit castRay(Player player, MapGrid map, double rayAngle) {

        final double rayDirX = Math.cos(rayAngle);
        final double rayDirY = Math.sin(rayAngle);

        int mapX = (int) player.getX();
        int mapY = (int) player.getY();

        final double deltaDistX = (rayDirX == 0) ? Double.POSITIVE_INFINITY : Math.abs(1.0 / rayDirX);
        final double deltaDistY = (rayDirY == 0) ? Double.POSITIVE_INFINITY : Math.abs(1.0 / rayDirY);

        final int stepX = (rayDirX < 0) ? -1 : 1;
        final int stepY = (rayDirY < 0) ? -1 : 1;

        final double px = player.getX();
        final double py = player.getY();

        double sideDistX;
        double sideDistY;

        if (stepX == 1) {
            double xDistToGrid = (mapX + 1.0) - px;
            sideDistX = xDistToGrid * deltaDistX;
        } else {
            double xDistToGrid = px - mapX;
            sideDistX = xDistToGrid * deltaDistX;
        }

        if (stepY == 1) {
            double yDistToGrid = (mapY + 1.0) - py;
            sideDistY = yDistToGrid * deltaDistY;
        } else {
            double yDistToGrid = py - mapY;
            sideDistY = yDistToGrid * deltaDistY;
        }

        boolean hit = false;
        boolean hitVerticalSide = false;

        final int maxSteps = map.getWidth() * map.getHeight();

        for (int i = 0; i < maxSteps && !hit; i++) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                hitVerticalSide = true;
            } else {

                sideDistY += deltaDistY;
                mapY += stepY;
                hitVerticalSide = false;
            }

            if (map.isWall(mapX, mapY)) {
                hit = true;
            }
        }

        final double perpDist = hitVerticalSide
                ? (sideDistX - deltaDistX)
                : (sideDistY - deltaDistY);

        return new RayHit(mapX, mapY, hitVerticalSide, perpDist);
    }

    private double computePerpendicularDistance(Player player, double rayAngle, RayHit hit) {
        return hit.perpendicularDistance * Math.cos(player.getAngle() - rayAngle);
    }

    private int computeWallSliceHeight(double wallHeight, double perpendicularDistance) {
        return (int) (wallHeight * projectionPlaneDistance / perpendicularDistance);

    }

    private double computeWallX(Player player, double rayAngle, RayHit hit) {
        double perp = hit.perpendicularDistance;
        if (hit.hitVerticalSide) {
            double hitY = player.getY() + perp * Math.sin(rayAngle);
            hitY = hitY - (int) hitY;
            return hitY;
        } else {
            double hitX = player.getX() + perp * Math.cos(rayAngle);
            hitX = hitX - (int) hitX;
            return hitX;
        }
    }

    private int computeWallTop(int sliceHeight, Player player, double wallHeight) {
        int center = screenHeight / 2;
        return center - sliceHeight / 2;
    }

    private int computeWallBottom(int wallTop, int sliceHeight) {
        return wallTop + sliceHeight;
    }

    private void drawVerticalSlice(PixelBuffer buffer, int screenX, int top, int bottom, int wallColor) {
        int ceilingColor = 0x202020;
        int floorColor = 0x404040;
        if (top < 0)
            top = 0;
        if (bottom > screenHeight)
            bottom = screenHeight;
        for (int y = 0; y < top; y++) {
            buffer.setPixel(screenX, y, ceilingColor);
        }
        for (int y = top; y < bottom; y++) {
            buffer.setPixel(screenX, y, wallColor);
        }
        for (int y = bottom; y < screenHeight; y++) {
            buffer.setPixel(screenX, y, floorColor);
        }
    }

    private void drawTexturedVerticalSlice(PixelBuffer buffer, Texture texture, int screenX, int top, int bottom,
            int textX) {
        int ceilingColor = 0x202020;
        int floorColor = 0x404040;
        if (top < 0)
            top = 0;
        if (bottom > screenHeight)
            bottom = screenHeight;
        for (int y = 0; y < top; y++) {
            buffer.setPixel(screenX, y, ceilingColor);
        }

        int sliceHeight = bottom - top;
        int textH = texture.getHeight();
        for (int y = top; y < bottom; y++) {
            int textY = (int) ((y - top) * textH / (double) sliceHeight);
            if (textY >= textH)
                textY = textH - 1;
            int rgb = texture.getPixel(textX, textY) & 0x00FFFFFF;
            buffer.setPixel(screenX, y, rgb);
        }
        for (int y = bottom; y < screenHeight; y++) {
            buffer.setPixel(screenX, y, floorColor);
        }
    }

    private int chooseWallColor(RayHit hit, int initColor) {
        if (hit.hitVerticalSide)
            return PixelBuffer.shadeDarker(initColor, 35);
        return initColor;
    }

    public static final class RayHit {
        public final int tileX;
        public final int tileY;

        public final boolean hitVerticalSide;

        public final double perpendicularDistance;

        public RayHit(int tileX, int tileY, boolean hitVerticalSide, double perpDistance) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.hitVerticalSide = hitVerticalSide;
            this.perpendicularDistance = perpDistance;
        }
    }
}
