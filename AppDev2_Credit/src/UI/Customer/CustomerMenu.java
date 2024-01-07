package UI.Customer;

import Backend.CreditListRenderer;
import Backend.CreditTableRow;
import UI.Login.Login;

import javax.swing.*;
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
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        creditList.setModel(listModel);
        creditList.setCellRenderer(new CreditListRenderer());

        getAllCustomerCredits(dbConnection, customerId);
        askForCreditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new CreditMenu(dbConnection, customerId);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Login(dbConnection);
            }
        });
    }

    private void getAllCustomerCredits(Connection dbConnection, int customerId ) {
        String selectQuery = "SELECT * FROM credit WHERE CustomerId = ? AND suggestion = false";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            stmt.setInt(1, customerId);
            try(ResultSet resultSet = stmt.executeQuery()){
                while (resultSet.next()) {
                    CreditTableRow row = new CreditTableRow();
                    row.ID = resultSet.getInt("ID");
                    row.CreditName = resultSet.getString("CreditName");
                    row.CreditSum = resultSet.getString("CreditSum");
                    row.TimeRange = resultSet.getString("CreditTimeRange");
                    row.PaymentInterval = resultSet.getString("PaymentInterval");
                    row.InterestRate = GetInterestRate(dbConnection, resultSet.getInt("InterestRateId"));
                    row.Status = resultSet.getString("Status");

                    listModel.addElement(row);
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
}
