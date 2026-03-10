import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The Entry Point of the program.
 * * @author Yanis Makhoukhi
 *
 */


public class launch {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new GUI().setVisible(true);
        });
    }
}
