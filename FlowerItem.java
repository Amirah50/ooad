import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class FlowerItem extends CreationItem {
    private BufferedImage image;
    private double scale = 1.0;

    public FlowerItem(BufferedImage image, int x, int y) {
        super(x, y, image.getWidth(), image.getHeight());
        this.image = image;
    }

    public void scale(double factor) {
        scale *= factor;
        // Update width and height based on new scale for accurate contains/draw
        this.width = (int)(image.getWidth() * scale);
        this.height = (int)(image.getHeight() * scale);
    }

    @Override
    public void draw(Graphics2D g2d) {
        Graphics2D g2dCopy = (Graphics2D) g2d.create(); // Create a copy

        // Translate to the item's position
        g2dCopy.translate(x, y);
        
        // Apply rotation around the center of the scaled image
        g2dCopy.rotate(Math.toRadians(rotation), (image.getWidth() / 2.0), (image.getHeight() / 2.0)); // Use original image dims for rotation origin
        // Note: The rotation origin here should be relative to the *unscaled* image coordinates if scale is applied later.
        // It's often easier to apply scale AFTER rotation if rotation origin is fixed to center.

        // Apply scaling
        g2dCopy.scale(scale, scale);
        
        g2dCopy.drawImage(image, 0, 0, null); // Draw at (0,0) relative to translated/rotated origin
        g2dCopy.dispose(); // Dispose the copy
    }

    public void scaleDown() {
        scale(0.9); // Reduce by 10%
    }

    public void scaleUp() {
        scale(1.1); // Increase by 10%
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }
}