package raycaster;

public class Player {
    private double x, y;
    private double angle;
    private double radius;
    private double moveSpeed;
    private double turnSpeed;

    public Player(double xPos, double yPos, double playerAngle, double playerRadius, double playerMoveSpeed,
            double playerTurnSpeed) {
        x = xPos;
        y = yPos;
        angle = playerAngle;
        radius = playerRadius;
        moveSpeed = playerMoveSpeed;
        turnSpeed = playerTurnSpeed;
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

    public void setPos(double x, double y) {
        this.y = y;
        this.x = x;
    }

}
