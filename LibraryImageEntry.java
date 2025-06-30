import java.awt.image.BufferedImage;

public class LibraryImageEntry { // Removed 'implements Serializable'
    private BufferedImage image;
    private String type; // e.g., "animal", "flower", "custom"

    public LibraryImageEntry(BufferedImage image, String type) {
        this.image = image;
        this.type = type;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}