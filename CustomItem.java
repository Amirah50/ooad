import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class CustomItem extends CreationItem {
    private BufferedImage image;

    public CustomItem(BufferedImage image, int x, int y) {
        super(x, y, image.getWidth(), image.getHeight());
        this.image = image;
    }

    @Override
    public void draw(Graphics2D g2d) {
        Graphics2D g2dCopy = (Graphics2D) g2d.create(); // Create a copy

        // Translate to the item's position
        g2dCopy.translate(x, y);

        // Apply rotation around the center of the image
        g2dCopy.rotate(Math.toRadians(rotation), width / 2.0, height / 2.0);
        
        g2dCopy.drawImage(image, 0, 0, null); // Draw at (0,0) relative to translated/rotated origin
        g2dCopy.dispose(); // Dispose the copy
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }
}