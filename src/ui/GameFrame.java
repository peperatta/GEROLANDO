package ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Gerolando");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        panel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}