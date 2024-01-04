package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class AdminMenu extends JFrame{
    private JButton logoutButton;
    private JPanel panel;
    private JButton createWorker;
    private JButton deleteWorker;

    public AdminMenu(Connection dbConnection, int workerId) {
        setContentPane(panel);
        setVisible(true);
        setTitle("Admin Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new Login(dbConnection);
            }
        });

        deleteWorker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new DeleteWorkerMenu(dbConnection, workerId);
            }
        });

        createWorker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new CreateWorkerMenu(dbConnection, workerId);
            }
        });
    }
}
