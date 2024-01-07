package UI.Superior;

import Backend.CreditListRenderer;
import Backend.CreditStatus;
import Backend.CreditTableRow;
import UI.Login.Login;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SuperiorMenu extends JFrame{
    private JButton showCreditsToAcceptButton;
    private JButton logoutButton;
    private JList allCredits;
    private JPanel panel;
    private DefaultListModel<CreditTableRow> creditListModel;
    public SuperiorMenu( Connection dbConnection, int workerId) {
        setContentPane(panel);
        setVisible(true);

        setTitle("Superior Menu");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        creditListModel = new DefaultListModel<>();
        allCredits.setModel(creditListModel);
        allCredits.setCellRenderer(new CreditListRenderer());

        loadInitialValues(dbConnection);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new Login(dbConnection);
            }
        });

        showCreditsToAcceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new AcceptCreditMenu(dbConnection, workerId);
            }
        });
    }

    private void loadInitialValues( Connection dbConnection ) {
        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT * FROM `credit` WHERE suggestion = ? AND Status not LIKE ? AND Status not LIKE ?");
            stmtCredit.setBoolean(1, true);
            stmtCredit.setString(2, CreditStatus.GENEHMIGT.toString());
            stmtCredit.setString(3, CreditStatus.ABGESCHLOSSEN.toString());
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                while (resultSet.next()) {
                    CreditTableRow creditTableRow = new CreditTableRow();
                    creditTableRow.ID = resultSet.getInt("ID");
                    creditTableRow.CreditName = resultSet.getString("CreditName");
                    creditTableRow.CreditSum = resultSet.getString("CreditSum");
                    creditTableRow.TimeRange = resultSet.getString("CreditTimeRange");
                    creditTableRow.PaymentInterval = resultSet.getString("PaymentInterval");
                    creditTableRow.Status = resultSet.getString("Status");
                    int originId = resultSet.getInt("originId");

                    if(checkSuggestions(dbConnection, originId)){
                        creditListModel.addElement(creditTableRow);
                    }
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    private boolean checkSuggestions( Connection dbConnection, int originId ) {
        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT Count(ID) as total FROM `credit` WHERE originId = ?");
            stmtCredit.setInt(1, originId);
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                if (resultSet.next()) {
                    int total = resultSet.getInt("total");
                    if(total == 2){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }
}
