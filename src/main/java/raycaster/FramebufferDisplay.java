package raycaster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FramebufferDisplay {
    static int WHITE = 0xFFFF;
    static int BLACK = 0x0000;
    static int SCREEN_WIDTH = 320;
    static int SCREEN_HEIGHT = 240;
    static String PATH = "/dev/fb_hat";
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

}
