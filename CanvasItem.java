import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class CanvasItem extends JComponent {
    protected BufferedImage image;
    protected double rotation;

    public CanvasItem(BufferedImage image, int x, int y) {
        this.image = image;
        setOpaque(false);
        setLocation(x, y);
        setSize(image.getWidth(), image.getHeight());
    }

    public void rotate(double degrees) {
        this.rotation = (this.rotation + degrees) % 360;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(rotation), getWidth() / 2, getHeight() / 2);
        g2d.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, this);
        g2d.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        double rad = Math.toRadians(-rotation);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int tx = (int) ((x - cx) * Math.cos(rad) - (y - cy) * Math.sin(rad) + cx);
        int ty = (int) ((x - cx) * Math.sin(rad) + (y - cy) * Math.cos(rad) + cy);
        return new Rectangle(0, 0, getWidth(), getHeight()).contains(tx, ty);
    }
}