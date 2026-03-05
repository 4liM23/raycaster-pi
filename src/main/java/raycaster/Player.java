package raycaster;

public class Player {
    private double x, y;
    private double angle;
    private double radius;
    private double moveSpeed;
    private double turnSpeed;
    private double eyeHeight;

    public Player(double xPos, double yPos, double playerAngle, double playerRadius, double playerMoveSpeed,
            double playerTurnSpeed, double eyeH) {
        x = xPos;
        y = yPos;
        angle = playerAngle;
        radius = playerRadius;
        moveSpeed = playerMoveSpeed;
        turnSpeed = playerTurnSpeed;
        eyeHeight = eyeH;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public double getRadius() {
        return radius;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public double getTurnSpeed() {
        return turnSpeed;
    }

    public double getEyeHeight() {
        return eyeHeight;
    }

    public void setPos(double x, double y) {
        this.y = y;
        this.x = x;
    }

    public void adjustAngle(int direction) {
        angle = (angle + direction * turnSpeed) % (2 * Math.PI);
    }

    public double getVector_X() {
        double dirX = Math.cos(angle);
        return moveSpeed * dirX;
    }

    public double getVector_Y() {
        double dirY = Math.sin(angle);
        return moveSpeed * dirY;
    }

}