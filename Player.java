import java.util.*;

public class Player {

    public String name;
    public float x, y, vx, vy, rotation, rotationVelocity;
    private ArrayList<Item> items = new ArrayList<>();

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