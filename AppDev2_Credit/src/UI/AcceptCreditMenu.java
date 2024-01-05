package UI;

import Backend.CreditBase;
import Backend.CreditStatus;
import Backend.CreditTableRow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AcceptCreditMenu extends JFrame{
    private JPanel panel;
    private JTextField firstSuggestion;
    private JTextField secondSuggestion;
    private JButton acceptFirstSuggestion;
    private JButton acceptSecondSuggestion;
    private JComboBox allCredits;
    private JButton cancleButton;

    private int firstSuggestionId;
    private int secondSuggestionId;

    public AcceptCreditMenu( Connection dbConnection, int workerId) {
        setContentPane(panel);
        setVisible(true);

        setTitle("Superior Menu");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadinitalValues(dbConnection);

        allCredits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCreditValues(dbConnection);
            }
        });

        cancleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new SuperiorMenu(dbConnection, workerId);
            }
        });

        acceptFirstSuggestion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                updateCredit(dbConnection, firstSuggestionId);
            }
        });

        acceptSecondSuggestion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                updateCredit(dbConnection, secondSuggestionId);
            }
        });
    }

    private void updateCredit( Connection dbConnection, int firstSuggestionId ) {
        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT * FROM `credit` WHERE ID = ?;");
            stmtCredit.setInt(1, firstSuggestionId);
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                if (resultSet.next()) {
                    int sum = resultSet.getInt("CreditSum");
                    int timeRange = resultSet.getInt("CreditTimeRange");
                    String interval = resultSet.getString("PaymentInterval");
                    int originId = resultSet.getInt("originId");

                    dbConnection.setAutoCommit(false);
                    PreparedStatement updateStmt = dbConnection.prepareStatement("UPDATE credit SET CreditSum = ?, CreditTimeRange = ?, PaymentInterval = ?, Status = ? Where ID = ? ;");
                    updateStmt.setInt(1, sum);
                    updateStmt.setInt(2, timeRange);
                    updateStmt.setString(3, interval);
                    if(sum < 500000){
                        updateStmt.setString(4, CreditStatus.GENEHMIGT.toString());
                    }else{
                        updateStmt.setString(4, CreditStatus.FOR_MANAGER.toString());
                    }
                    updateStmt.setInt(5, originId);

                    updateStmt.executeUpdate();
                    dbConnection.commit();
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void selectCreditValues( Connection dbConnection ) {
        CreditBase selectedCreditBase = (CreditBase) allCredits.getSelectedItem();
        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT * FROM `credit` WHERE originId = ?;");
            stmtCredit.setInt(1, selectedCreditBase.OriginId);
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                int count = 1;
                while (resultSet.next()) {
                    String value = "Summe: "
                            + resultSet.getString("CreditSum") + ", Zeitslot: "
                            + resultSet.getString("CreditTimeRange") + ", Interval : "
                            + resultSet.getString("PaymentInterval") + ", CustomerId: "
                            + resultSet.getString("CustomerId");
                    if(count == 1){
                        firstSuggestion.setText(value);
                        firstSuggestionId = resultSet.getInt("ID");
                    }
                    else{
                        secondSuggestion.setText(value);
                        secondSuggestionId = resultSet.getInt("ID");
                    }
                    count++;
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void loadinitalValues( Connection dbConnection ) {
        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT * FROM `credit` WHERE suggestion = ?");
            stmtCredit.setBoolean(1, true);
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                while (resultSet.next()) {
                    CreditBase creditBase = new CreditBase();
                    creditBase.Id = resultSet.getInt("ID");
                    creditBase.Name = resultSet.getString("CreditName");
                    creditBase.CreditSum = resultSet.getInt("CreditSum");
                    creditBase.OriginId = resultSet.getInt("originId");
                    int originId = resultSet.getInt("originId");

                    if(checkSuggestions(dbConnection, originId)){
                        allCredits.addItem(creditBase);
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
