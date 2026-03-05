package raycaster;

public class Movement {
    static final int A = 0, B = 1, X = 2, Y = 3;

    public void actOnInput(Player p, MapGrid map, boolean[] inputs) {
        int moveDir = 0;
        int turnDir = 0;
        // A button >>> left
        if (inputs[A])
            turnDir--;
        // B button >>> right
        if (inputs[B])
            turnDir++;
        // X button >>> forward
        if (inputs[X])
            moveDir++;
        // Y button >>> backward
        if (inputs[Y])
            moveDir--;

        p.adjustAngle(turnDir);
        double dx = moveDir * p.getVector_X();
        double dy = moveDir * p.getVector_Y();
        if (dx != 0 || dy != 0)
            Collision.movePlayer(p, map, dx, dy);
    }
}
