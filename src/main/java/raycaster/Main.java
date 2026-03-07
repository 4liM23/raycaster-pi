package raycaster;

// import java.io.*;

public class Main {
    static final int UPS = 120; // updates per second
    static final int FOV = 60;

    public static void main(String[] args) throws Exception {
        FramebufferDisplay display = new FramebufferDisplay();
        PixelBuffer buffer = new PixelBuffer();
        ButtonReader buttons = new ButtonReader();
        Movement movement = new Movement();
        MapGrid map = new MapGrid();
        Player player = new Player(
                2.0, 2.0, 0,
                0.35, 0.5, 0.4,
                0.5);
        Raycaster caster = new Raycaster(PixelBuffer.SCREEN_WIDTH, PixelBuffer.SCREEN_HEIGHT, FOV);

        final long stepNs = 1_000_000_000L / UPS;

        long lastTime = System.nanoTime();
        long accumulator = 0L;

        int frameCount = 0;
        long t1 = 0, t2 = 0, t3 = 0, t4 = 0;

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

            // debug
            t1 = System.nanoTime();
            caster.renderWalls(player, map, buffer);
            t2 = System.nanoTime();
            display.writeFrame(buffer.getBuffer());
            t3 = System.nanoTime();

            frameCount++;
            if (frameCount % 60 == 0) {
                System.out.println(
                        "Frame: " + frameCount +
                                "\nRender: " + (t2 - t1) +
                                "\nWrite buffer: " + (t3 - t2));
            }

            long sleepNs = stepNs - accumulator;
            if (sleepNs > 1_000_000L) {
                Thread.sleep(sleepNs / 1_000_000L);
            }
        }
    }
}