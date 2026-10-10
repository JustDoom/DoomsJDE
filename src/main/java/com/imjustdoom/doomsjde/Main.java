import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.imjustdoom.doomsjde.ProjectSelector;
import javax.swing.*;

void main() {
    FlatDraculaIJTheme.setup();
    SwingUtilities.invokeLater(() -> new ProjectSelector().setVisible(true));
}