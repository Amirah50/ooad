import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageTransferHandler extends TransferHandler {
    // Define a custom DataFlavor for our item type String
    public static final DataFlavor ITEM_TYPE_FLAVOR = new DataFlavor(String.class, "Item Type");

    private LibraryImageEntry entry; // Still holds LibraryImageEntry on the source side
    private JComponent targetCanvas;

    public ImageTransferHandler(LibraryImageEntry entry, JComponent targetCanvas) {
        this.entry = entry;
        this.targetCanvas = targetCanvas;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        // Create a Transferable that offers both the image and the type separately
        return new ImageAndTypeSelection(entry.getImage(), entry.getType());
    }

    @Override
    public boolean canImport(TransferSupport support) {
        // We can import if the image flavor is supported.
        // We *prefer* if ITEM_TYPE_FLAVOR is also supported to get specific item type.
        return support.isDataFlavorSupported(DataFlavor.imageFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        // Always require image flavor
        if (!support.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return false;
        }

        try {
            Transferable t = support.getTransferable();
            BufferedImage droppedImage = (BufferedImage) t.getTransferData(DataFlavor.imageFlavor);
            String itemType = "custom"; // Default type

            // Check if our custom type flavor is available
            if (support.isDataFlavorSupported(ITEM_TYPE_FLAVOR)) {
                itemType = (String) t.getTransferData(ITEM_TYPE_FLAVOR);
            }

            if (droppedImage == null) return false;

            Point dropPoint = support.getDropLocation().getDropPoint();

            if (targetCanvas instanceof LeftCanvas canvas) {
                CreationItem newItem = null;
                switch (itemType) {
                    case "animal":
                        newItem = new AnimalItem(droppedImage, dropPoint.x, dropPoint.y);
                        break;
                    case "flower":
                        newItem = new FlowerItem(droppedImage, dropPoint.x, dropPoint.y);
                        break;
                    case "custom":
                    default: // Default to CustomItem for any unknown or non-specific type
                        newItem = new CustomItem(droppedImage, dropPoint.x, dropPoint.y);
                        break;
                }
                canvas.addItem(newItem);
                return true;
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // Inner class for the Transferable object
    static class ImageAndTypeSelection implements Transferable {
        private BufferedImage image;
        private String type;

        public ImageAndTypeSelection(BufferedImage image, String type) {
            this.image = image;
            this.type = type;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor)) {
                return image;
            } else if (flavor.equals(ITEM_TYPE_FLAVOR)) {
                return type; // Return the String type
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            // Offer both image flavor and our custom type flavor
            return new DataFlavor[]{DataFlavor.imageFlavor, ITEM_TYPE_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.imageFlavor) || flavor.equals(ITEM_TYPE_FLAVOR);
        }
    }
}