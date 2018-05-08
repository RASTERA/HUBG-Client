public class Bullet {
    public double vx, vy, x, y, angle;

    public Bullet(double x, double y, double vx, double vy, double angle) {
        this.vx = vx;
        this.vy = vy;
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void update() {
        this.x += Math.cos(Math.toRadians(angle)) * this.vx;
        this.y += Math.sin(Math.toRadians(angle)) * this.vy;
    }
}