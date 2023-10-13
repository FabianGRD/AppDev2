package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame
{
    JFrame frame;
    JButton login, register, exit;
    JButton profil, creditRequest;
    JPanel panel;
    public Menu(){
        createFrame();
    }

    public void createFrame(){
        frame = new JFrame();
        frame.setSize(900,750);
        frame.setVisible(true);
    }

    public void createLogin(){
        login = new JButton("Login");
        register = new JButton("Register");
        exit = new JButton("Exit");
        panel = new JPanel();
        panel.add(login);
        panel.add(register);
        panel.add(exit);
        frame.add(panel);
        frame.validate();
        frame.repaint();

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.removeAll();
                frame.remove(panel);
                frame.validate();
                frame.repaint();
                createMenuCustomer();
            }
        });
    }

    public void createMenuCustomer(){
        createFrame();
        panel = new JPanel();
        profil = new JButton("Profil");
        creditRequest = new JButton("Kredit Anfrage");
        exit = new JButton("Exit");
        panel = new JPanel();
        panel.add(profil);
        panel.add(creditRequest);
        panel.add(exit);
        createTableWithRequests();
        frame.validate();
        frame.repaint();
    }

    public void createTableWithRequests(){
        login = new JButton("Login");
        register = new JButton("Register");
        exit = new JButton("Exit");

        String[] columnNames = {"ID",
                "Kredit Name",
                "Verwendungszweck",
                "Summe",
                "Zinssatz",
                "Bonität",
                "Status"};
        JTable table = new JTable(null, columnNames);

        panel.add(table);
        frame.add(panel);
    }
}
