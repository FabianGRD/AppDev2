package UI;

import javax.swing.*;

public class CustomerMenu extends JFrame {
    private JPanel panel;
    private JButton kreditBeantragenButton;
    private JButton zurückButton;
    private JTable table1;
    private JButton logoutButton;

    public CustomerMenu(){
        setContentPane(panel);
        setVisible(true);
        setTitle("Customer Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
