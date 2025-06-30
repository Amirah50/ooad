import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ToolBar extends JToolBar {
    private final LeftCanvas leftCanvas;
    private final RightCanvas rightCanvas;
    private final LibraryManager libraryManager;

    public ToolBar(LeftCanvas leftCanvas, RightCanvas rightCanvas, LibraryManager libraryManager) {
        this.leftCanvas = leftCanvas;
        this.rightCanvas = rightCanvas;
        this.libraryManager = libraryManager;
        buildToolbar();
    }

    private void buildToolbar() {
        // Button to clear the Left Canvas (Composition)
        JButton btnCreateLeftCanvas = createToolbarButton("New Left Canvas", "Clear and start new composition on Left Canvas", e -> createNewCanvas());
        add(btnCreateLeftCanvas);

        // Button to clear the Right Canvas (Freehand Drawing)
        JButton btnClearRightDrawing = createToolbarButton("Clear Right Drawing", "Clear freehand drawing on Right Canvas", e -> refreshDrawing());
        add(btnClearRightDrawing);

        JButton btnColorPicker = createToolbarButton("Color", "Pick Pen Color", e -> openColorPicker());
        add(btnColorPicker);

        // Pen Size Selector
        Integer[] penSizes = {1, 2, 4, 8, 12, 16, 24};
        JComboBox<Integer> penSizeSelector = new JComboBox<>(penSizes);
        penSizeSelector.setSelectedItem((int) rightCanvas.getStrokeWidth());
        penSizeSelector.setMaximumSize(new Dimension(80, 30));
        penSizeSelector.setToolTipText("Pen Size");
        penSizeSelector.addActionListener(e -> {
            int size = (Integer) penSizeSelector.getSelectedItem();
            changePenStrokeWidth(size);
        });
        add(penSizeSelector);

        // Stroke Style Selector
        String[] strokeStyles = {"Solid", "Dashed", "Dotted"};
        JComboBox<String> strokeStyleSelector = new JComboBox<>(strokeStyles);
        strokeStyleSelector.setSelectedItem("Solid");
        strokeStyleSelector.setMaximumSize(new Dimension(80, 30));
        strokeStyleSelector.setToolTipText("Stroke Style");
        strokeStyleSelector.addActionListener(e -> {
            String style = (String) strokeStyleSelector.getSelectedItem();
            changePenStrokeStyle(style);
        });
        add(strokeStyleSelector);

        // Save Button for Right Canvas drawing
        JButton btnSaveRightCanvas = createToolbarButton("Save Right Drawing", "Save freehand drawing as image", e -> saveDrawingAsImage());
        add(btnSaveRightCanvas);

        // Merge Right Canvas onto Left Canvas
        JButton btnMerge = createToolbarButton("Merge Canvases", "Merge Right Canvas to Left Canvas", e -> mergeCanvases());
        add(btnMerge);

        // Rotate Left Canvas
        JButton btnRotateLeftCanvas = createToolbarButton("Rotate Left Canvas", "Rotate the entire Left Canvas by 90 degrees", e -> rotateLeftCanvas());
        add(btnRotateLeftCanvas);
    }

    private JButton createToolbarButton(String text, String tooltip, ActionListener action) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(action);
        return button;
    }

    // Clears the Left Canvas
    private void createNewCanvas() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear the Left Canvas? This action cannot be undone.",
                "Confirm Clear Left Canvas", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            leftCanvas.clear();
        }
    }

    // Clears the Right Canvas
    private void refreshDrawing() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear the Right Canvas drawing? This action cannot be undone.",
                "Confirm Clear Right Canvas", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            rightCanvas.clear();
        }
    }

    private void openColorPicker() {
        Color newColor = JColorChooser.showDialog(this, "Choose Pen Color", rightCanvas.getPenColor());
        if (newColor != null) {
            rightCanvas.setPenColor(newColor);
        }
    }

    private void changePenStrokeWidth(int size) {
        rightCanvas.setPenStroke(new BasicStroke(size));
    }

    private void changePenStrokeStyle(String style) {
        switch (style) {
            case "Solid":
                rightCanvas.setPenStroke(new BasicStroke(rightCanvas.getStrokeWidth()));
                break;
            case "Dashed":
                float[] dashingPattern = {10f, 10f};
                rightCanvas.setPenStroke(new BasicStroke(rightCanvas.getStrokeWidth(),
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f, dashingPattern, 0.0f));
                break;
            case "Dotted":
                float[] dottedPattern = {2f, 2f}; // Dotted pattern: 2px dash, 2px gap
                rightCanvas.setPenStroke(new BasicStroke(rightCanvas.getStrokeWidth(),
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f, dottedPattern, 0.0f));
                break;
            default:
                rightCanvas.setPenStroke(new BasicStroke(rightCanvas.getStrokeWidth()));
        }
    }

    private void saveDrawingAsImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Right Canvas Drawing");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG Images", "jpg", "jpeg"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                BufferedImage img = rightCanvas.getCanvasImage();
                String ext = getFileExtension(fileToSave.getName());

                if (ext.isEmpty()) { // If no extension, default to PNG
                    ext = "png";
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                } else if (!ext.equalsIgnoreCase("png") && !ext.equalsIgnoreCase("jpg") && !ext.equalsIgnoreCase("jpeg")) {
                    // If unsupported extension, default to PNG and inform user
                    JOptionPane.showMessageDialog(this, "Unsupported file extension. Saving as PNG.", "Warning", JOptionPane.WARNING_MESSAGE);
                    ext = "png";
                    fileToSave = new File(fileToSave.getAbsolutePath().substring(0, fileToSave.getAbsolutePath().lastIndexOf('.')) + ".png");
                }

                ImageIO.write(img, ext, fileToSave);
                JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Add the saved image to the library as a "custom" type
                if (libraryManager != null) {
                    libraryManager.addItemToLibrary(img, "custom"); // Specify type as "custom"
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex < 0) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    private void mergeCanvases() {
        // Confirm before merging to avoid accidental overwrites
        int confirm = JOptionPane.showConfirmDialog(this,
                "Merge the current Right Canvas drawing onto the Left Canvas?",
                "Confirm Merge", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            leftCanvas.mergeWith(rightCanvas.getSnapshot());
        }
    }

    private void rotateLeftCanvas() {
        leftCanvas.rotateCanvas(90);
    }
}