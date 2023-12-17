package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminMenu_Alt {
    private JFrame frame;
    JButton creditRequest, logout, back;
    JPanel panel;

    public AdminMenu_Alt( JFrame frame){
        this.frame = frame;
    }

    public void createMenuAdmin(){
        panel = new JPanel();
        creditRequest = new JButton("Kredite prüfen");
        logout = new JButton("Logout");

        panel.add(creditRequest);
        panel.add(logout);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();

        creditRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                frame.remove(panel);
                frame.revalidate();
                frame.repaint();
                checkCreditRequest();
            }
        });

        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                frame.remove(panel);
                frame.revalidate();
                frame.repaint();
                Menu_alt menuAlt = new Menu_alt();
                menuAlt.createLogin();
            }
        });
    }

    private void checkCreditRequest(){
        panel = new JPanel();
        back = new JButton("<- Zurück");

        panel.add(back);

        createTableWithRequests();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                frame.remove(panel);
                frame.revalidate();
                frame.repaint();
                createMenuAdmin();
            }
        });
    }
    private void createTableWithRequests(){
        String[] columnNames = {"ID", "Kunden ID", "Verwendungszweck", "Summe", "Zinssatz", "Laufzeit", "Status"};

        Object[][] data = {
                {"1","12345", "Auto", 50000, "4%", "5 Jahre", "zu prüfen"},
                {"2","12345", "Haus", 500000, "4%", "30 Jahre", "Prüfung durch Vorgesetzten"},
                {"3","12345", "Auto2", 100000, "4%", "5 Jahre", "zu prüfen"},
        };

        JTable table = new JTable(data, columnNames);

        panel.add(table);
    }
}
