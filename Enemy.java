public class Enemy {
    private final String name;
    private float x;
    private float y;
    public float vx;
    public float vy;
    private float rotation;
    public float rotationVelocity;
    private final int id;

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
        return this.id;
    }
}
