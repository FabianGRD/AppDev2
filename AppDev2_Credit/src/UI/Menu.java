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
        frame.revalidate();
        frame.repaint();

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(panel);
                frame.revalidate();
                frame.repaint();
                createMenuCustomer();
            }
        });
    }

    public void createMenuCustomer(){
        panel = new JPanel();
        profil = new JButton("Profil");
        creditRequest = new JButton("Kredit Anfrage");
        exit = new JButton("Exit");

        panel.add(profil);
        panel.add(creditRequest);
        panel.add(exit);

        createTableWithRequests();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    public void createTableWithRequests(){
        String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian"};

        Object[][] data = {
                {"Kathy", "Smith",
                        "Snowboarding", 5, true},
                {"John", "Doe",
                        "Rowing", 5, true},
                {"Sue", "Black",
                        "Knitting", 5,true},
                {"Jane", "White",
                        "Speed reading", 5, true},
                {"Joe", "Brown",
                        "Pool", 5, true}
        };

        JTable table = new JTable(data, columnNames);

        panel.add(table);
    }
}
