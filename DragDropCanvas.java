import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class DragDropCanvas extends JPanel {
    private BufferedImage bufferImage;
    private Graphics2D g2d;
    private Point location = new Point(100, 100);

    public DragDropCanvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = bufferImage.createGraphics();
        setBackground(Color.WHITE);
    }

    public void addItem(BufferedImage image, Point loc) {
        Graphics2D g = bufferImage.createGraphics();
        g.drawImage(image, loc.x, loc.y, null);
        g.dispose();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bufferImage, 0, 0, null);
    }
}