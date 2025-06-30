import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;

public class FileHandler {

    public static void saveImage(BufferedImage image, Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Image");
        int result = fc.showSaveDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String format = getFileExtension(file).toLowerCase();

            try {
                if (!format.equals("png") && !format.equals("jpg")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                ImageIO.write(image, format, file);
                JOptionPane.showMessageDialog(parent, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static BufferedImage loadImage(Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Open Image");
        int result = fc.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                return ImageIO.read(fc.getSelectedFile());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex == -1) ? "" : name.substring(dotIndex + 1);
    }
}