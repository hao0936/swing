package app;

import controller.AppController;
import model.UserSession;
import view.LoginDialog;
import view.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Path root = Paths.get("").toAbsolutePath();
                AppController controller = new AppController(root);
                LoginDialog loginDialog = new LoginDialog(null, controller);
                loginDialog.setVisible(true);
                UserSession session = loginDialog.getSession();
                if (session == null) {
                    return;
                }
                MainFrame frame = new MainFrame(controller, session);
                frame.setVisible(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Failed to load CSV data: " + ex.getMessage());
            }
        });
    }
}
