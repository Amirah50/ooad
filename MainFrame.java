import java.awt.BorderLayout;
import javax.swing.*;

public class MainFrame extends JFrame {
    private final LeftCanvas leftCanvas;
    private final RightCanvas rightCanvas;
    private final LibraryManager libraryManager;

    public MainFrame() {
        setTitle("Drawing Studio Pro - Dual Canvas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Initialize canvases
        leftCanvas = new LeftCanvas();
        rightCanvas = new RightCanvas();

        // Initialize LibraryManager
        libraryManager = new LibraryManager("library_assets");

        // Create toolbar, passing the libraryManager instance
        JToolBar toolBar = new ToolBar(leftCanvas, rightCanvas, libraryManager);

        // Add library panel
        JPanel libraryPanel = libraryManager.getLibraryPanel(leftCanvas);

        // Layout setup
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCanvas, rightCanvas);
        splitPane.setDividerLocation(600);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, libraryPanel, splitPane);
        mainSplitPane.setDividerLocation(200);

        add(toolBar, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Ensure Swing UI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame app = new MainFrame();
            app.setVisible(true);
        });
    }
}