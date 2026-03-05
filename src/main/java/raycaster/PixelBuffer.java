package raycaster;

public class PixelBuffer {
    static final int WHITE = 0xFFFF;
    static final int BLACK = 0x0000;
    static final int BLUE = 0x001f;
    static final int YELLOW = 0xff80;
    static final int RED = 0xf800;
    static final int SCREEN_WIDTH = 320;
    static final int SCREEN_HEIGHT = 240;
    private final byte[] data = new byte[SCREEN_WIDTH * SCREEN_HEIGHT * 2];

    public byte[] getBuffer() {
        return data;
    }

    public void clear() {
        clear(BLACK);
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < SCREEN_WIDTH && y >= 0 && y < SCREEN_HEIGHT;
    }

    public void clear(int rgb) {
        for (int i = 0; i < data.length; i += 2) {
            data[i] = (byte) (rgb & 0xFF);
            data[i + 1] = (byte) ((rgb >> 8) & 0xFF);
        }
    }

    public void setPixel(int x, int y, int rgb) {
        if (!inBounds(x, y))
            return;
        int index = (y * SCREEN_WIDTH + x) * 2;
        data[index] = (byte) (rgb & 0xFF);
        data[index + 1] = (byte) ((rgb >> 8) & 0xFF);
    }

    public static int shadeDarker(int rgb565, double darkenPercent) {
        if (darkenPercent < 0)
            darkenPercent = 0;
        if (darkenPercent > 100)
            darkenPercent = 100;

        double factor = 1.0 - (darkenPercent / 100.0);

        int r5 = (rgb565 >>> 11) & 0x1F;
        int g6 = (rgb565 >>> 5) & 0x3F;
        int b5 = rgb565 & 0x1F;

        r5 = (int) Math.round(r5 * factor);
        g6 = (int) Math.round(g6 * factor);
        b5 = (int) Math.round(b5 * factor);

        return (r5 << 11) | (g6 << 5) | b5;
    }

    public void fillRect(int x, int y, int w, int h, int rgb) {
        for (int row = y; row < SCREEN_HEIGHT && row < y + h; row++) {
            for (int col = x; col < SCREEN_WIDTH && col < x + w; col++) {
                setPixel(col, row, rgb);
            }
        }
    }

    public void fillCircle(int x, int y, int r, int rgb) {
        for (int degree = 0; degree < 360; degree++) {
            double x1 = r * Math.cos(degree * (Math.PI / 180));
            double y1 = r * Math.sin(degree * (Math.PI / 180));
            int ElX = (int) Math.round(x + x1);
            int ElY = (int) Math.round(y + y1);
            setPixel(ElX, ElY, rgb);
        }
    }

    public void drawLine(int x1, int y1, int len, double angle, int rgb) {
        int x2 = x1 + (int) Math.round(len * Math.cos(angle));
        int y2 = y1 + (int) Math.round(len * Math.sin(angle));

        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {
            setPixel(x1, y1, rgb);
            return;
        }

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;

            int x = (int) Math.round(x1 + t * dx);
            int y = (int) Math.round(y1 + t * dy);

            setPixel(x, y, rgb);
        }
    }

}
