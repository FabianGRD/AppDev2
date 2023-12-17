package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerMenu_alt {

    private JFrame frame;
    JButton profil, creditRequest, logout;
    JPanel panel;

    public CustomerMenu_alt( JFrame frame){
        this.frame = frame;
    }

    public void createMenuCustomer(){
        panel = new JPanel();
        profil = new JButton("Profil");
        creditRequest = new JButton("Kredit Anfrage");
        logout = new JButton("Logout");

        panel.add(profil);
        panel.add(creditRequest);
        panel.add(logout);

        createTableWithRequests();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();

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

    public void createTableWithRequests(){
        String[] columnNames = {"ID", "Verwendungszweck", "Summe", "Zinssatz", "Laufzeit", "Status"};

        Object[][] data = {
                {"1", "Auto", 50000, "4%", "5 Jahre", "genehmigt"},
                {"2", "Haus", 500000, "4%", "30 Jahre", "in Prüfung"},
                {"3", "Auto2", 100000, "4%", "5 Jahre", "abgelehnt"},
        };

        JTable table = new JTable(data, columnNames);

        panel.add(table);
    }
}
