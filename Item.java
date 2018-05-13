import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public abstract class Item {

    private BufferedImage thumbnail;
    private String name;


    public Item(String name, String thumbnail) {
        try {
            this.thumbnail = ImageIO.read(new File(thumbnail));
        }
        catch (Exception e) {

        }

        this.name = name;
    }

    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    public String getName() {
        return name;
    }
}