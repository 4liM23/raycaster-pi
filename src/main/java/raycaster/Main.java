package raycaster;

// import java.io.*;

public class Main {
    static final int FPS = 30;
    static final int FOV = 60;

    public static void main(String[] args) throws Exception {
        FramebufferDisplay display = new FramebufferDisplay();
        PixelBuffer buffer = new PixelBuffer();
        ButtonReader buttons = new ButtonReader();
        Movement movement = new Movement();
        MapGrid map = new MapGrid();
        Player player = new Player(2.0, 2.0, 0, 0.35, 0.20, 0.12, 0.5);
        // TopDownRenderer renderer = new TopDownRenderer();
        Raycaster caster = new Raycaster(PixelBuffer.SCREEN_WIDTH, PixelBuffer.SCREEN_HEIGHT, FOV);

        long tick = 1000_000_000 / FPS;
        long now = System.nanoTime();
        long nextTick = now + tick;
        int frameCount = 0;
        long t1 = 0, t2 = 0, t3 = 0, t4 = 0;

        while (true) {
            now = System.nanoTime();
            if (now >= nextTick) {
                nextTick += tick;
                frameCount++;
                t1 = System.nanoTime();
                movement.actOnInput(player, map, buttons.buttonsState());
                t2 = System.nanoTime();
                // renderer.render(map, player, buffer);
                caster.renderWalls(player, map, buffer);
                t3 = System.nanoTime();
                display.writeFrame(buffer.getBuffer());
                t4 = System.nanoTime();
            } else {
                Thread.sleep((nextTick - now) / 1000_000);
            }
            if (frameCount % 60 == 0) {
                System.out.println("Frame: " + frameCount + "\nInput & movement: " + (t2 - t1) + "\nRender: "
                        + (t3 - t2) + "\nWrite buffer: " + (t4 - t3));
            }
        }

    }
}