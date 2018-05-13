public class Enemy {
    public String name;
    public float x, y, vx, vy, rotation, rotationVelocity;
    private int id;

    public Enemy(String name, float[] info) {
        this.name = name;
        this.x = info[0];
        this.y = info[1];
        this.rotation = info[2];
        this.id = (int) info[3];
    }

    public void update(float[] newLocation) {
        this.x = newLocation[0];
        this.y = newLocation[1];
        this.rotation = newLocation[2];
    }

    public int getId() {
        return id;
    }
}
