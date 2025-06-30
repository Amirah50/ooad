import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LeftCanvas extends JPanel {
    private BufferedImage compositeImage;
    private List<CreationItem> items = new ArrayList<>();
    private double rotationAngle = 0; // Canvas rotation
    private CreationItem selectedItem = null;
    private int lastX, lastY; // Instance variables for mouse drag tracking

    public LeftCanvas() {
        setBorder(BorderFactory.createTitledBorder("Left Canvas - Composition"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 600));
        compositeImage = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        clear();

        // Mouse listeners for item selection and dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedItem = null; // Deselect previous item
                // Iterate backwards to select the top-most item
                for (int i = items.size() - 1; i >= 0; i--) {
                    CreationItem item = items.get(i);
                    // Check if the mouse click is within the bounds of the item
                    if (item.contains(e.getX(), e.getY())) {
                        selectedItem = item;
                        lastX = e.getX(); // Initialize lastX, lastY for dragging
                        lastY = e.getY();
                        break;
                    }
                }

                // Handle right-click for transformations on selected item
                if (SwingUtilities.isRightMouseButton(e) && selectedItem != null) {
                    showTransformationMenu(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // selectedItem = null; // Can keep item selected after release if needed for menu
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null) {
                    int dx = e.getX() - lastX;
                    int dy = e.getY() - lastY;

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        selectedItem.translate(dx, dy); // Transpose (move)
                    } else if (e.isControlDown() && SwingUtilities.isLeftMouseButton(e)) { // Ctrl + Left-Click for rotation
                        // Calculate rotation based on mouse movement relative to item center
                        int itemCenterX = selectedItem.getX() + selectedItem.getWidth() / 2;
                        int itemCenterY = selectedItem.getY() + selectedItem.getHeight() / 2;

                        double currentAngle = Math.atan2(e.getY() - itemCenterY, e.getX() - itemCenterX);
                        double lastAngle = Math.atan2(lastY - itemCenterY, lastX - itemCenterX);
                        double rotationDegrees = Math.toDegrees(currentAngle - lastAngle);
                        
                        selectedItem.rotate(rotationDegrees);
                    } else if (e.isShiftDown() && SwingUtilities.isLeftMouseButton(e)) { // Shift + Left-Click for scaling (FlowerItem specific)
                         if (selectedItem instanceof FlowerItem flower) {
                             int itemCenterX = selectedItem.getX() + selectedItem.getWidth() / 2;
                             int itemCenterY = selectedItem.getY() + selectedItem.getHeight() / 2;

                             // Calculate distance from center for scaling
                             double currentDist = Point.distance(itemCenterX, itemCenterY, e.getX(), e.getY());
                             double lastDist = Point.distance(itemCenterX, itemCenterY, lastX, lastY);

                             if (lastDist > 0) { // Avoid division by zero
                                 double factor = currentDist / lastDist;
                                 flower.scale(factor);
                             }
                         }
                    }
                    
                    lastX = e.getX();
                    lastY = e.getY();
                    repaint();
                }
            }
        });
    }

    private void showTransformationMenu(Component invoker, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem flipItem = new JMenuItem("Flip (Animal)");
        flipItem.addActionListener(e -> {
            if (selectedItem instanceof AnimalItem animal) {
                animal.flip();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Flip only applies to Animal items.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        popupMenu.add(flipItem);

        JMenuItem scaleUpItem = new JMenuItem("Scale Up (Flower)");
        scaleUpItem.addActionListener(e -> {
            if (selectedItem instanceof FlowerItem flower) {
                flower.scaleUp();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Scale only applies to Flower items.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        popupMenu.add(scaleUpItem);

        JMenuItem scaleDownItem = new JMenuItem("Scale Down (Flower)");
        scaleDownItem.addActionListener(e -> {
            if (selectedItem instanceof FlowerItem flower) {
                flower.scaleDown();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Scale only applies to Flower items.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        popupMenu.add(scaleDownItem);

        JMenuItem rotateRightItem = new JMenuItem("Rotate Right (90°)");
        rotateRightItem.addActionListener(e -> {
            if (selectedItem != null) {
                selectedItem.rotate(90);
                repaint();
            }
        });
        popupMenu.add(rotateRightItem);
        
        JMenuItem rotateLeftItem = new JMenuItem("Rotate Left (90°)");
        rotateLeftItem.addActionListener(e -> {
            if (selectedItem != null) {
                selectedItem.rotate(-90);
                repaint();
            }
        });
        popupMenu.add(rotateLeftItem);

        popupMenu.show(invoker, x, y);
    }

    public void mergeWith(BufferedImage image) {
        Graphics2D g2d = compositeImage.createGraphics();
        // Clear the current composite image before drawing the new one if you want a clean merge
        // g2d.clearRect(0, 0, compositeImage.getWidth(), compositeImage.getHeight());
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        repaint();
    }

    public void rotateCanvas(double angle) {
        rotationAngle = (rotationAngle + angle) % 360;
        if (rotationAngle < 0) {
            rotationAngle += 360;
        }
        repaint();
    }

    public void addItem(CreationItem item) {
        items.add(item);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Apply the canvas's overall rotation
        AffineTransform canvasTransform = new AffineTransform();
        canvasTransform.rotate(Math.toRadians(rotationAngle), getWidth() / 2.0, getHeight() / 2.0);
        g2d.setTransform(canvasTransform);

        // Draw the base composite image
        if (compositeImage != null) {
            g2d.drawImage(compositeImage, 0, 0, null);
        }

        // Draw individual items (they apply their own transformations)
        for (CreationItem item : items) {
            item.draw(g2d);
        }

        g2d.dispose();
    }

    public void clear() {
        Graphics2D g2d = compositeImage.createGraphics();
        g2d.setBackground(getBackground());
        g2d.clearRect(0, 0, compositeImage.getWidth(), compositeImage.getHeight());
        g2d.dispose();
        items.clear(); // Clear individual items as well
        rotationAngle = 0; // Reset canvas rotation
        selectedItem = null; // Deselect any item
        repaint();
    }

    public BufferedImage getSnapshot() {
        // Create a blank image to draw everything onto, considering canvas rotation
        BufferedImage snapshot = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = snapshot.createGraphics();
        
        // Apply the *same* canvas transform to the snapshot graphics
        AffineTransform snapshotTransform = new AffineTransform();
        snapshotTransform.rotate(Math.toRadians(rotationAngle), getWidth() / 2.0, getHeight() / 2.0);
        g2d.setTransform(snapshotTransform);

        // Draw the composite image and all individual items
        if (compositeImage != null) {
            g2d.drawImage(compositeImage, 0, 0, null);
        }
        for (CreationItem item : items) {
            item.draw(g2d);
        }
        
        g2d.dispose();
        return snapshot;
    }
}