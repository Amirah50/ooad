import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class LibraryManager {
    // Change list type to LibraryImageEntry
    private List<LibraryImageEntry> libraryItems = new ArrayList<>();
    private final File libraryDir;

    public LibraryManager(String folderName) {
        this.libraryDir = new File(folderName);
        if (!libraryDir.exists()) libraryDir.mkdirs();
        loadSavedItems();
    }

    // Now accepts type for the item being added
    public void addItemToLibrary(BufferedImage image, String type) {
        LibraryImageEntry newEntry = new LibraryImageEntry(image, type);
        libraryItems.add(newEntry);
        saveItemToFile(newEntry);
    }

    // Saves the LibraryImageEntry
    private void saveItemToFile(LibraryImageEntry entry) {
        // Use the type and a timestamp for the filename
        File file = new File(libraryDir, entry.getType() + "_" + System.currentTimeMillis() + ".png");
        try {
            ImageIO.write(entry.getImage(), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSavedItems() {
        libraryItems.clear();

        // Load predefined images from project root with inferred types
        String[] imagePaths = {
            "animal1.png", "animal2.png",
            "custom1.png", "custom2.png",
            "flower1.png", "flower2.png"
        };

        for (String path : imagePaths) {
            try {
                BufferedImage img = ImageIO.read(getClass().getResource("/" + path)); // Use getResource for classpath
                String type = "custom"; // Default type

                // Infer type based on filename prefix
                if (path.startsWith("animal")) {
                    type = "animal";
                } else if (path.startsWith("flower")) {
                    type = "flower";
                }
                libraryItems.add(new LibraryImageEntry(img, type));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Error loading predefined image: " + path + " - " + e.getMessage());
            }
        }

        // Load user-saved items from library directory
        File[] files = libraryDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg"));
        if (files != null) {
            for (File file : files) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    String type = "custom"; // Default type for saved items

                    // Infer type from filename for saved items
                    String fileName = file.getName().toLowerCase();
                    if (fileName.startsWith("animal_")) {
                        type = "animal";
                    } else if (fileName.startsWith("flower_")) {
                        type = "flower";
                    } // 'custom_' or 'item_' are typically for custom items by default

                    libraryItems.add(new LibraryImageEntry(img, type));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JPanel getLibraryPanel(JComponent targetCanvas) {
        JPanel panel = new JPanel(new GridLayout(0, 3, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Library"));

        for (LibraryImageEntry entry : libraryItems) { // Iterate over LibraryImageEntry
            JLabel label = createDraggableLabel(entry, targetCanvas); // Pass the entry
            panel.add(label);
        }

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            panel.removeAll();
            loadSavedItems(); // Reloads all items, including newly saved ones
            for (LibraryImageEntry entry : libraryItems) {
                JLabel label = createDraggableLabel(entry, targetCanvas);
                panel.add(label);
            }
            panel.revalidate();
            panel.repaint();
        });

        panel.add(refreshBtn);
        return panel;
    }

    // Now accepts LibraryImageEntry
    private JLabel createDraggableLabel(LibraryImageEntry entry, JComponent targetCanvas) {
        JLabel label = new JLabel(new ImageIcon(entry.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH)));
        label.setTransferHandler(new ImageTransferHandler(entry, targetCanvas)); // Pass the entry
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                JComponent comp = (JComponent) me.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, me, TransferHandler.COPY);
            }
        });
        return label;
    }
}