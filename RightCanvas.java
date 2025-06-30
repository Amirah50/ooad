import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class RightCanvas extends JPanel {
    private BufferedImage canvasImage;
    private Graphics2D g2d;
    private Color penColor = Color.BLACK;
    private Stroke penStroke = new BasicStroke(4.0f);
    private Point lastPoint = null;
    private float strokeWidth = 4.0f; // Track stroke width for consistent style changes

    public RightCanvas() {
        setBorder(BorderFactory.createTitledBorder("Right Canvas - Freehand Drawing"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 600));
        initializeCanvas();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                lastPoint = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (lastPoint != null) {
                    g2d.setColor(penColor);
                    g2d.setStroke(penStroke);
                    g2d.drawLine(lastPoint.x, lastPoint.y, e.getX(), e.getY());
                    lastPoint = e.getPoint();
                    repaint();
                }
            }
        });
    }

    private void initializeCanvas() {
        canvasImage = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        g2d = canvasImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        clear(); // Clear the canvas initially
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvasImage, 0, 0, null);
    }

    public void clear() {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        // Reset to default drawing color/stroke after clearing background
        g2d.setColor(penColor);
        g2d.setStroke(penStroke);
        repaint();
    }

    public void setPenColor(Color color) {
        this.penColor = color;
        // Apply new color immediately to g2d for future drawing
        g2d.setColor(penColor);
    }

    public void setPenStroke(Stroke stroke) {
        this.penStroke = stroke;
        // Extract width from BasicStroke to maintain it for style changes
        if (stroke instanceof BasicStroke bs) {
            this.strokeWidth = bs.getLineWidth();
        }
        // Apply new stroke immediately to g2d for future drawing
        g2d.setStroke(penStroke);
    }

    public BufferedImage getCanvasImage() {
        return canvasImage;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public BufferedImage getSnapshot() {
        BufferedImage snapshot = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        paint(snapshot.getGraphics()); // Draws the current visual state of the panel
        return snapshot;
    }

    public void saveImage() {
        FileHandler.saveImage(canvasImage, this);
    }

    public Color getPenColor() {
        return penColor;
    }
}