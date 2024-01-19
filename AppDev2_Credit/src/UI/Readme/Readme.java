package UI.Readme;

import UI.Login.Login;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class Readme extends JFrame{
    private JPanel panel;
    private JLabel Admin;
    private JButton returnButton;

    public Readme( Connection dbConnection) {
        setContentPane(panel);
        setVisible(true);
        setTitle("Readme");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new Login(dbConnection);
            }
        });
    }
}
