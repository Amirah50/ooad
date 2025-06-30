import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class CreationItem {
    protected int x, y;
    protected double rotation;
    protected int width, height;

    public CreationItem(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = 0; // Initialize rotation
    }

    public abstract void draw(Graphics2D g2d);

    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public boolean contains(int checkX, int checkY) {
        // Create an affine transform for the item's current state (position and rotation)
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(Math.toRadians(rotation), width / 2.0, height / 2.0);

        // Inverse transform the mouse coordinates to the item's local coordinate system
        try {
            AffineTransform inverseTransform = transform.createInverse();
            Point transformedPoint = new Point();
            inverseTransform.transform(new Point(checkX, checkY), transformedPoint);
            
            // Check if the transformed point is within the item's unrotated bounds at (0,0)
            return new Rectangle(0, 0, width, height).contains(transformedPoint.x, transformedPoint.y);
        } catch (java.awt.geom.NoninvertibleTransformException e) {
            e.printStackTrace();
            return false; // Should not happen in typical 2D transformations
        }
    }

    public void rotate(double degrees) {
        this.rotation = (this.rotation + degrees) % 360;
        if (this.rotation < 0) {
            this.rotation += 360;
        }
    }

    public double getRotation() {
        return rotation;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public abstract BufferedImage getImage();

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}