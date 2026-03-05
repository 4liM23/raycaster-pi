package raycaster;

public class Collision {

    public static void movePlayer(Player p, MapGrid map, double dx, double dy) {
        double r = p.getRadius();
        double xPos = p.getX();
        double yPos = p.getY();
        double newXPos, newYPos;
        // x position check
        int upper = (int) xPos, lower = (int) xPos;
        while (!map.isWall(upper, (int) yPos)) {
            upper++;
        }
        while (!map.isWall(lower, (int) yPos)) {
            lower--;
        }
        lower++;

        if (dx > 0) {
            double allowed = upper - (xPos + r);
            double move = Math.min(dx, Math.max(0.0, allowed));
            newXPos = xPos + move;
        } else {
            double allowed = (xPos - r) - lower;
            double move = Math.min(-dx, Math.max(0.0, allowed));
            newXPos = xPos - move;
        }

        // y position check
        upper = (int) yPos;
        lower = (int) yPos;
        while (!map.isWall((int) newXPos, upper)) {
            upper++;
        }
        while (!map.isWall((int) newXPos, lower)) {
            lower--;
        }
        lower++;
        if (dy > 0) {
            double allowed = upper - (yPos + r);
            double move = Math.min(dy, Math.max(0.0, allowed));
            newYPos = yPos + move;
        } else {
            double allowed = (yPos - r) - lower;
            double move = Math.min(-dy, Math.max(0.0, allowed));
            newYPos = yPos - move;
        }

        p.setPos(newXPos, newYPos);
    }
}
