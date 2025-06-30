import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class CanvasUtils {
    public static BufferedImage rotateImage(BufferedImage img, double degrees) {
        double radians = Math.toRadians(degrees);
        AffineTransform transform = AffineTransform.getRotateInstance(radians, img.getWidth() / 2, img.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(img, null);
    }

    public static BufferedImage mergeImages(BufferedImage base, BufferedImage overlay) {
        BufferedImage combined = new BufferedImage(
            base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combined.createGraphics();
        g2d.drawImage(base, 0, 0, null);
        g2d.drawImage(overlay, 0, 0, null);
        g2d.dispose();
        return combined;
    }
}