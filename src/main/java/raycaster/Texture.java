package raycaster;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Texture {
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final int[] pixels565;

    public Texture(String path) {
        try {
            image = ImageIO.read(new File(path));
            width = image.getWidth();
            height = image.getHeight();
            pixels565 = new int[width * height];
            int p = 0;
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    pixels565[p++] = PixelBuffer.from32To16Rgb(image.getRGB(c, r));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture: " + path, e);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixel(int x, int y) {
        return pixels565[width * y + x];
    }
}