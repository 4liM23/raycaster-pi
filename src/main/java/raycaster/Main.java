package raycaster;

import java.io.*;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

public class Main {
    static ButtonReader buttons = new ButtonReader();
    static int WHITE = 0xFFFF;
    static int BLACK = 0x0000;
    static int SCREEN_WIDTH = 320;
    static int SCREEN_HEIGHT = 240;
    static int FPS = 60;
    static int A_BUTTON_GPIO = 5;
    static int B_BUTTON_GPIO = 6;
    static int X_BUTTON_GPIO = 16;
    static int Y_BUTTON_GPIO = 24;

    private static void pixelToBuffer(byte[] buffer, int pixel, int row, int col) {
        int index = (row * SCREEN_WIDTH + col) * 2;
        buffer[index] = (byte) ((pixel >> 8) & 0xFF);
        buffer[index + 1] = (byte) (pixel & 0xFF);
    }

    private static void frameToBuffer(byte[] buffer, int[][] frame) {
        for (int row = 0; row < SCREEN_HEIGHT; row++) {
            for (int col = 0; col < SCREEN_WIDTH; col++) {
                pixelToBuffer(buffer, frame[row][col], row, col);
            }
        }
    }

    private static void drawSquare(int[][] frame, int x, int y, int size, int color) {
        for (int row = y; row < y + size; row++) {
            for (int col = x; col < x + size; col++) {
                if (row >= 0 && row < SCREEN_HEIGHT && col >= 0 && col < SCREEN_WIDTH) {
                    frame[row][col] = color;
                }
            }
        }
    }

    private static void clearFrame(int[][] frame) {
        for (int row = 0; row < SCREEN_HEIGHT; row++) {
            for (int col = 0; col < SCREEN_WIDTH; col++) {
                frame[row][col] = BLACK;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        int[][] frame = new int[SCREEN_HEIGHT][SCREEN_WIDTH];
        byte[] buffer = new byte[SCREEN_WIDTH * SCREEN_HEIGHT * 2];
        // fb0 is the framebuffer device on Linux
        RandomAccessFile fb = new RandomAccessFile("/dev/fb_hat", "rw");
        long tick = 1000000000 / FPS;
        long nextTick = System.nanoTime() + tick;
        int squareX = 0;
        int squareY = 100;
        int squareSize = 20;

        int frameCount = 0;

        while (frameCount < FPS * 60) {
            long now = System.nanoTime();
            if (now >= nextTick) {
                nextTick += tick;
                drawSquare(frame, squareX, squareY, squareSize, WHITE);
                frameToBuffer(buffer, frame);
                fb.seek(0);
                fb.write(buffer);

                if (buttons.A_Button_Pressed()) {
                    squareX -= 2;
                }
                if (buttons.B_Button_Pressed()) {
                    squareY -= 2;
                }
                if (buttons.X_Button_Pressed()) {
                    squareX += 2;
                }
                if (buttons.Y_Button_Pressed()) {
                    squareY += 2;
                }
                if (squareX > SCREEN_WIDTH - squareSize) {
                    squareX = 0;
                }
                if (squareY > SCREEN_HEIGHT - squareSize) {
                    squareY = 0;
                }
                if (squareX < 0) {
                    squareX = SCREEN_WIDTH - squareSize;
                }
                if (squareY < 0) {
                    squareY = SCREEN_HEIGHT - squareSize;
                }
                clearFrame(frame);
                frameCount++;
            } else {
                Thread.sleep((nextTick - now) / 1000000);
            }
        }
        fb.close();

    }
}

class ButtonReader {
    Context pi4j;
    DigitalInput btnA, btnB, btnX, btnY;

    public ButtonReader() {
        pi4j = Pi4J.newAutoContext();
        DigitalInputConfigBuilder cfg;
        cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("btnA")
                .address(5) // BCM GPIO number
                .pull(PullResistance.PULL_UP);
        btnA = pi4j.create(cfg);

        cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("btnB")
                .address(6) // BCM GPIO number
                .pull(PullResistance.PULL_UP);
        btnB = pi4j.create(cfg);

        cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("btnX")
                .address(16) // BCM GPIO number
                .pull(PullResistance.PULL_UP);
        btnX = pi4j.create(cfg);

        cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("btnY")
                .address(24) // BCM GPIO number
                .pull(PullResistance.PULL_UP);
        btnY = pi4j.create(cfg);
    }

    public boolean A_Button_Pressed() {
        return btnA.isLow();
    }

    public boolean B_Button_Pressed() {
        return btnB.isLow();
    }

    public boolean X_Button_Pressed() {
        return btnX.isLow();
    }

    public boolean Y_Button_Pressed() {
        return btnY.isLow();
    }
}
