package UI;

import Backend.CreditTableRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMenu extends JFrame {
    private JPanel panel;
    private JButton askForCreditButton;
    private JButton logoutButton;
    private JList<CreditTableRow> creditList;

    private DefaultListModel<CreditTableRow> listModel;

    public CustomerMenu(Connection dbConnection, int customerId){
        setContentPane(panel);
        setVisible(true);
        setTitle("Customer Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize list model
        listModel = new DefaultListModel<>();
        creditList.setModel(listModel);
        creditList.setCellRenderer(new CreditListRenderer()); // Custom renderer

        getAllCustomerCredits(dbConnection, customerId);
        askForCreditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                //String selectedUserType = (String) userTypeComboBox.getSelectedItem();
                new CreditMenu(dbConnection, customerId);
            }
        });


    }

    private void getAllCustomerCredits(Connection dbConnection, int customerId ) {
        String selectQuery = "SELECT * FROM credit WHERE CustomerId = ?";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            stmt.setInt(1, customerId);
            try(ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()) {
                    CreditTableRow row = new CreditTableRow();
                    row.CreditName = resultSet.getString("CreditName");
                    row.CreditSum = resultSet.getString("CreditSum");
                    row.TimeRange = resultSet.getString("CreditTimeRange");
                    row.InterestRate = GetInterestRate(dbConnection, resultSet.getInt("InterestRateId"));
                    row.Status = "genehmigt";

                    listModel.addElement(row);
                }else{
                    System.out.println("Kein Kredit gefunden!");
                }
            }
        }catch (SQLException exception){
            System.out.println(exception);
        }
    }
    private String GetInterestRate(Connection dbConnection, int interestRateId){
        String selectQuery = "SELECT * FROM initialcreditvalues where ID = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            stmt.setInt(1, interestRateId);
            try(ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()) {
                    String interestRateFromDB = resultSet.getString("InterestRate");
                    return interestRateFromDB;
                }else{
                    System.out.println("Kein Zinssatz gefunden");
                }
            }
        }catch (SQLException exception){
            System.out.println(exception);
        }

        return "";
    }

    private class CreditListRenderer extends DefaultListCellRenderer {
        public java.awt.Component getListCellRendererComponent(JList list,
                                                               Object value,
                                                               int index,
                                                               boolean isSelected,
                                                               boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof CreditTableRow) {
                CreditTableRow credit = (CreditTableRow) value;
                setText("Credit: " + credit.CreditName +
                        ", Summe: " + credit.CreditSum+
                        ", Zeitspanne: " + credit.TimeRange+
                        ", Zins: " + credit.InterestRate+
                        ", Status: " + credit.Status);
            }

            return this;
        }
    }
}
