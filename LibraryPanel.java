import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class LibraryPanel extends JPanel {
    private final LibraryManager libraryManager;

    public LibraryPanel(LeftCanvas leftCanvas) {
        this.libraryManager = new LibraryManager("library_assets");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Library"));

        add(libraryManager.getLibraryPanel(leftCanvas));
    }
}