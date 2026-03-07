package raycaster;

// import java.io.*;

public class Main {
    static final int UPS = 120; // updates per second
    static final int FOV = 60;

    public static void main(String[] args) throws Exception {
        // FramebufferDisplay display = new FramebufferDisplay();
        // PixelBuffer buffer = new PixelBuffer();
        // ButtonReader buttons = new ButtonReader();
        // Movement movement = new Movement();
        // MapGrid map = new MapGrid();
        // Player player = new Player(
        // 2.0, 2.0, 0,
        // 0.35, 0.05, 0.04,
        // 0.5);
        // Raycaster caster = new Raycaster(PixelBuffer.SCREEN_WIDTH,
        // PixelBuffer.SCREEN_HEIGHT, FOV);
        long s0 = System.nanoTime();
        FramebufferDisplay display = new FramebufferDisplay();
        long s1 = System.nanoTime();
        PixelBuffer buffer = new PixelBuffer();
        long s2 = System.nanoTime();
        ButtonReader buttons = new ButtonReader();
        long s3 = System.nanoTime();
        Movement movement = new Movement();
        long s4 = System.nanoTime();
        MapGrid map = new MapGrid();
        long s5 = System.nanoTime();
        Player player = new Player(
                2.0, 2.0, 0,
                0.35, 0.05, 0.04,
                0.5);
        long s6 = System.nanoTime();
        Raycaster caster = new Raycaster(PixelBuffer.SCREEN_WIDTH, PixelBuffer.SCREEN_HEIGHT, FOV);
        long s7 = System.nanoTime();

        // debug
        System.out.println("FramebufferDisplay: " + (s1 - s0));
        System.out.println("PixelBuffer: " + (s2 - s1));
        System.out.println("ButtonReader: " + (s3 - s2));
        System.out.println("Movement: " + (s4 - s3));
        System.out.println("MapGrid: " + (s5 - s4));
        System.out.println("Player: " + (s6 - s5));
        System.out.println("Raycaster: " + (s7 - s6));

        final long stepNs = 1_000_000_000L / UPS;

        long lastTime = System.nanoTime();
        long accumulator = 0L;

        while (true) {
            long now = System.nanoTime();
            long frameTime = now - lastTime;
            lastTime = now;

            if (frameTime > 250_000_000L) {
                frameTime = 250_000_000L; // safety cap
            }

            accumulator += frameTime;

            int updates = 0;
            while (accumulator >= stepNs && updates < 5) {
                movement.actOnInput(player, map, buttons.buttonsState());
                accumulator -= stepNs;
                updates++;
            }
            caster.renderWalls(player, map, buffer);
            display.writeFrame(buffer.getBuffer());

            long sleepNs = stepNs - accumulator;
            if (sleepNs > 1_000_000L) {
                Thread.sleep(sleepNs / 1_000_000L);
            }
        }
    }
}