package raycaster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class FramebufferDisplay {
    static final int WHITE = 0xFFFF;
    static final int BLACK = 0x0000;
    static final int SCREEN_WIDTH = 320;
    static final int SCREEN_HEIGHT = 240;
    static final String PATH = "/dev/fb_hat";
    private RandomAccessFile fb;

    public FramebufferDisplay() throws InterruptedException, FileNotFoundException {
        fb = new RandomAccessFile(PATH, "rw");
    }

    public void writeFrame(byte[] buffer) throws IOException {
        fb.seek(0);
        fb.write(buffer);
    }

    public void close() throws IOException {
        fb.close();
    }

    public static void saveScreenshot(PixelBuffer buffer, String path) {
        int width = PixelBuffer.SCREEN_WIDTH;
        int height = PixelBuffer.SCREEN_HEIGHT;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb565 = buffer.getPixel(x, y);
                int rgb888 = PixelBuffer.from16To32Rgb(rgb565);
                image.setRGB(x, y, rgb888);
            }
        }

        image = upscale(image, 4);

        try {
            ImageIO.write(image, "png", new File(path));
            System.out.println("Saved screenshot: " + path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }

    public static BufferedImage upscale(BufferedImage source, int scale) {
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        BufferedImage upscaled = new BufferedImage(
                srcWidth * scale,
                srcHeight * scale,
                BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < srcHeight; y++) {
            for (int x = 0; x < srcWidth; x++) {
                int rgb = source.getRGB(x, y);

                int startX = x * scale;
                int startY = y * scale;

                for (int dy = 0; dy < scale; dy++) {
                    for (int dx = 0; dx < scale; dx++) {
                        upscaled.setRGB(startX + dx, startY + dy, rgb);
                    }
                }
            }
        }

        return upscaled;
    }

}
