package raycaster;

// import java.io.*;

public class Main {
    static final int FPS = 30;

    public static void main(String[] args) throws Exception {
        FramebufferDisplay display = new FramebufferDisplay();
        PixelBuffer buffer = new PixelBuffer();
        ButtonReader buttons = new ButtonReader();
        Movement movement = new Movement();
        MapGrid map = new MapGrid();
        Player player = new Player(2.0, 2.0, 0, 0.25, 0.2, 0.2);
        TopDownRenderer renderer = new TopDownRenderer();

        long tick = 1000_000_000 / FPS;
        long now = System.nanoTime();
        long nextTick = now + tick;

        while (true) {
            now = System.nanoTime();
            if (now >= nextTick) {
                nextTick += tick;
                movement.actOnInput(player, map, buttons.buttonsState());
                renderer.render(map, player, buffer);
                display.writeFrame(buffer.getBuffer());
            } else {
                Thread.sleep((nextTick - now) / 1000_000);
            }
        }

    }
}