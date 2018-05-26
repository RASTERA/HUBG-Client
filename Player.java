import java.util.*;

public class Player {

    private final String name;
    private final float x;
    private final float y;
    public float vx;
    public float vy;
    private final float rotation;
    public float rotationVelocity;
    private final ArrayList<Item> items = new ArrayList<>();

    public Player(String name, float x, float y, float rotation) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public ArrayList<Item> getItems() {
        return this.items;
    }

}