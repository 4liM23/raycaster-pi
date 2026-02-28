package raycaster;

import java.io.*;

public class Main {
    static int WHITE = 0xFFFF;
    static int BLACK = 0x0000;
    static int SCREEN_WIDTH = 320;
    static int SCREEN_HEIGHT = 240;
    static int FPS = 30;

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
        RandomAccessFile fb = new RandomAccessFile("/dev/fb0", "rw");
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
                squareX += 1;
                if (squareX > SCREEN_WIDTH - squareSize) {
                    squareX = 0;
                    squareY += 1;
                    if (squareY > SCREEN_HEIGHT - squareSize) {
                        squareY = 0;
                    }
                }
                clearFrame(frame);
                frameCount++;
            } else {
                Thread.sleep((nextTick - now) / 1000000);
            }
        }

    }
}