import java.util.HashMap;
import java.awt.*;

public class Tile {

    public enum Types {GRASS, WALL, WATER, TOWN}

    private final HashMap<String, Types> tiles = new HashMap<String, Types>() {
        {
            this.put("g", Types.GRASS);
            this.put("t", Types.WALL);
            this.put("w", Types.WATER);
        }
    };

    private final HashMap<Tile.Types, Color> colorLookup = new HashMap<Tile.Types, Color>() {
        {
            this.put(Tile.Types.GRASS, new Color(0, 255, 0));
            this.put(Tile.Types.WATER, new Color(0, 0, 255));
            this.put(Tile.Types.WALL, new Color(255, 0, 0));
        }
    };

    private final Types type;
    private final Color color;

    public Tile(String type) {
        this.type = this.tiles.get(type);
        this.color = this.colorLookup.get(this.type);
    }

    public Tile(Types type) {
        this.type = type;
        this.color = this.colorLookup.get(this.type);
    }

    public String toString() {
        return "Me is da " + this.type;
    }
}