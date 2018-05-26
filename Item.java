import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public abstract class Item {

    private BufferedImage thumbnail;
    private final String name;


    public Item(String name, String thumbnail) {
        try {
            this.thumbnail = ImageIO.read(new File(thumbnail));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.name = name;
    }

    public BufferedImage getThumbnail() {
        return this.thumbnail;
    }

    public String getName() {
        return this.name;
    }
}