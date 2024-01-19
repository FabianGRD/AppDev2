package UI.Manager;

import Backend.CreditBase;
import Backend.CreditStatus;
import UI.Login.Login;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerMenu extends JFrame {
    private JPanel panel;
    private JButton logoutButton;
    private JComboBox allCredits;
    private JButton acceptButton;
    private JButton cancelButton;

    public ManagerMenu( Connection dbConnection, int workerId) {
        setContentPane(panel);
        setVisible(true);

        setTitle("Manager Menu");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadInitialValues(dbConnection);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new Login(dbConnection);
            }
        });

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                updateCredit(dbConnection, true);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                updateCredit(dbConnection, false);
            }
        });
    }

    private void updateCredit( Connection dbConnection, boolean permisson) {
        CreditBase creditBase = (CreditBase) allCredits.getSelectedItem();
        try {
                dbConnection.setAutoCommit(false);
                PreparedStatement updateStmt = dbConnection.prepareStatement("UPDATE credit SET Status = ? Where ID = ? ;");
                if(permisson){
                    updateStmt.setString(1, CreditStatus.GENEHMIGT.toString());
                }else {
                    updateStmt.setString(1, CreditStatus.ABGELEHNT.toString());
                }
                updateStmt.setInt(2, creditBase.Id);

                updateStmt.executeUpdate();
                dbConnection.commit();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        allCredits.removeAllItems();
        loadInitialValues(dbConnection);
    }

    private void loadInitialValues( Connection dbConnection ) {
        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT ID, CreditName, CreditSum, CustomerId, InterestRateId FROM `credit` WHERE status LIKE ?;");
            stmtCredit.setString(1, CreditStatus.FOR_MANAGER.toString());
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                while (resultSet.next()) {
                    CreditBase creditBase = new CreditBase();
                    creditBase.Id = resultSet.getInt("ID");
                    creditBase.Name = resultSet.getString("CreditName");
                    creditBase.CreditSum = resultSet.getInt("CreditSum");
                    creditBase.CustomerId = resultSet.getInt("CustomerId");
                    creditBase.InterestRateId = resultSet.getInt("InterestRateId");
                    allCredits.addItem(creditBase);
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
