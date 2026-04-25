import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("All Hands on Deck");
        Player1 gamePanel = new Player1();
        frame.add(gamePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new Timer(50, e -> gamePanel.repaint()).start();
    }
}