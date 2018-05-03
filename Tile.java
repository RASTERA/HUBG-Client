public class Tile {

    public enum Types {GRASS, WALL, WATER}
    public Types type;

    public Tile(Types type) {
        this.type = type;
    }
}