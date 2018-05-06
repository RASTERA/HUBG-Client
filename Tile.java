import java.util.HashMap;
import javax.swing.*;
import java.awt.*;

public class Tile {

    public static enum Types {GRASS, WALL, WATER, TOWN};

    public HashMap<String, Types> tiles = new HashMap<String, Types>() {
        {
            put("g", Types.GRASS);
            put("t", Types.WALL);
            put("w", Types.WATER);
        }
    };

    private HashMap<Tile.Types, Color> colorLookup = new HashMap<Tile.Types, Color>() {
        {
            put(Tile.Types.GRASS, new Color(0, 255, 0));
            put(Tile.Types.WATER, new Color(0, 0, 255));
        }
    };

    public Types type;
    public Color color;

    public Tile(String type) {
        this.type = tiles.get(type);
        this.color = colorLookup.get(this.type);
    }

    public Tile(Types type) {
        this.type = type;
        this.color = colorLookup.get(this.type);
    }

    public String toString() {
        return "Me is da " + type;
    }
}