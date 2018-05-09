public class Bullet {
    public double velocity, x, y, angle;

    public Bullet(double x, double y, double velocity, double angle) {
        this.velocity = velocity;
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void update() {
        this.x += Math.cos(Math.toRadians(angle)) * this.velocity;
        this.y += Math.sin(Math.toRadians(angle)) * this.velocity;
    }
}