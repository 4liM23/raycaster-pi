package raycaster;

public class Raycaster {

    private final int screenWidth;
    private final int screenHeight;

    private final double horizontalFov;
    private final double projectionPlaneDistance;
    private final double planeScale;

    private final Texture wallTexture;

    public Raycaster(int screenWidth, int screenHeight, double horizontalFov) {
        this.wallTexture = new Texture("assets/textures/walls/test.png");
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.horizontalFov = horizontalFov * (Math.PI / 180);
        this.planeScale = Math.tan(this.horizontalFov / 2.0);
        this.projectionPlaneDistance = (screenWidth / 2) / this.planeScale;
    }

    public void renderWalls(Player player, MapGrid map, PixelBuffer buffer) {

        buffer.clear();
        RayHit hit = new RayHit();
        double rayAngle = player.getAngle() - horizontalFov / 2 + 0.5 * horizontalFov / screenWidth;
        double angleStep = horizontalFov / screenWidth;
        double dirX = Math.cos(player.getAngle());
        double dirY = Math.sin(player.getAngle());
        double planeX = -dirY * planeScale;
        double planeY = dirX * planeScale;
        for (int col = 0; col < screenWidth; col++) {
            rayAngle += angleStep;
            double cameraX = 2.0 * col / screenWidth - 1.0;
            double rayDirX = dirX + planeX * cameraX;
            double rayDirY = dirY + planeY * cameraX;

            castRay(player, map, rayDirX, rayDirY, hit);

            double perp = computePerpendicularDistance(player, rayAngle, hit);

            int sliceHeight = computeWallSliceHeight(map.getWallHeight(), perp);

            int top = computeWallTop(sliceHeight, player, map.getWallHeight());

            int bottom = computeWallBottom(top, sliceHeight);

            int wallX = (int) (wallTexture.getWidth() * computeWallX(player, rayAngle, hit));

            // added to account for texture mirroring
            if (hit.hitVerticalSide && Math.cos(rayAngle) > 0) {
                wallX = wallTexture.getWidth() - wallX - 1;
            }

            if (!hit.hitVerticalSide && Math.sin(rayAngle) < 0) {
                wallX = wallTexture.getWidth() - wallX - 1;
            }
            drawTexturedVerticalSlice(buffer, wallTexture, hit, col, top, bottom, wallX);
            // drawVerticalSlice(buffer, col, top, bottom, color);

        }
    }

    private double computeRayAngle(Player player, int screenX) {
        return player.getAngle() - horizontalFov / 2 + (screenX + 0.5) * horizontalFov / screenWidth;
    }

    private void castRay(Player player, MapGrid map, double rayDirX, double rayDirY, RayHit rayHit) {

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

        rayHit.storeData(mapX, mapY, hitVerticalSide, perpDist);
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

    private void drawTexturedVerticalSlice(PixelBuffer buffer, Texture texture, RayHit hit, int screenX, int top,
            int bottom,
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
            int rgb = chooseWallColorFromTexture(hit, texture, textX, textY, true);
            buffer.setPixel(screenX, y, rgb);
        }
        for (int y = bottom; y < screenHeight; y++) {
            buffer.setPixel(screenX, y, floorColor);
        }
    }

    private int chooseWallColorFromTexture(RayHit hit, Texture texture, int textX, int textY, boolean shading) {
        int rgb = PixelBuffer.from32To16Rgb(texture.getPixel(textX, textY));
        if (shading && hit.hitVerticalSide)
            return PixelBuffer.shadeDarker(rgb, 35);
        return rgb;
    }

    public static final class RayHit {
        public int tileX;
        public int tileY;
        public boolean hitVerticalSide;
        public double perpendicularDistance;

        public void storeData(int tileX, int tileY, boolean hitVerticalSide, double perpDistance) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.hitVerticalSide = hitVerticalSide;
            this.perpendicularDistance = perpDistance;
        }
    }
}
