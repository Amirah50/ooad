import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class AnimalItem extends CreationItem {
    private BufferedImage image;
    private boolean flipped = false;

    public AnimalItem(BufferedImage image, int x, int y) {
        super(x, y, image.getWidth(), image.getHeight());
        this.image = image;
    }

    public void flip() {
        flipped = !flipped;
    }

    @Override
    public void draw(Graphics2D g2d) {
        Graphics2D g2dCopy = (Graphics2D) g2d.create(); // Create a copy to not affect other items

        // Translate to the item's position
        g2dCopy.translate(x, y);

        // Apply rotation around the center of the image
        g2dCopy.rotate(Math.toRadians(rotation), width / 2.0, height / 2.0);

        // Apply flip if needed
        if (flipped) {
            g2dCopy.scale(-1, 1);
            g2dCopy.translate(-width, 0); // Adjust translation after scaling
        }
        
        g2dCopy.drawImage(image, 0, 0, null); // Draw at (0,0) relative to translated/rotated origin
        g2dCopy.dispose(); // Dispose the copy
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }
}