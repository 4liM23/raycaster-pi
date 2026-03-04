package raycaster;

public class Collision {

    // helpers
    private double min(double a, double b) {
        return a < b ? a : b;
    }

    public void movePlayer(Player p, MapGrid map, double dx, double dy) {
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
        if (dx > 0) {
            newXPos = xPos + min(dx, upper - (xPos + r));
        } else {
            newXPos = xPos - min(dx, (xPos - r) - lower);
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
        if (dy > 0) {
            newYPos = yPos + min(dy, upper - (yPos + r));
        } else {
            newYPos = yPos - min(dy, (yPos - r) - lower);
        }

        p.setPos(newXPos, newYPos);
    }
}
