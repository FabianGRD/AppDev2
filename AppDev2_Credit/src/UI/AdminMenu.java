package UI;

import javax.swing.*;

public class AdminMenu extends JFrame{
    private JButton kreditButton;
    private JButton zurückButton;
    private JButton logoutButton;
    private JTable table1;
    private JPanel panel;

    public AdminMenu() {
        setContentPane(panel);
        setVisible(true);
        setTitle("Admin Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
