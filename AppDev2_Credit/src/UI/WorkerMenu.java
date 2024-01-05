package UI;

import Backend.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WorkerMenu extends JFrame {
    private JPanel panel;
    private JButton logoutButton;
    private JButton showCustomers;
    private JList<CustomerTableRow> customerList;
    private JList<CreditTableRow>  creditList;
    private JButton showCredits;
    private DefaultListModel<CreditTableRow> creditListModel;
    private DefaultListModel<CustomerTableRow> customerListModel;

    public WorkerMenu( Connection dbConnection, int workerId) {
        setContentPane(panel);
        setVisible(true);

        setTitle("Worker Menu");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        creditListModel = new DefaultListModel<>();
        creditList.setModel(creditListModel);
        creditList.setCellRenderer(new CreditListRenderer());

        customerListModel = new DefaultListModel<>();
        customerList.setModel(customerListModel);
        customerList.setCellRenderer(new CustomerListRenderer());

        loadInitialValues(dbConnection);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new Login(dbConnection);
            }
        });

        showCustomers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new ShowCustomerMenu(dbConnection, workerId);
            }
        });

        showCredits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new ShowCustomerCredits(dbConnection, workerId);
            }
        });
    }

    private void loadInitialValues( Connection dbConnection ) {
        try {
            PreparedStatement stmtCustomer = dbConnection.prepareStatement("SELECT * FROM customer;");
            try (ResultSet resultSet = stmtCustomer.executeQuery()) {
                while (resultSet.next()) {
                    if(resultSet.getString("bonitaet") == null){
                        CustomerTableRow customerTableRow = new CustomerTableRow();
                        customerTableRow.ID = resultSet.getString("ID");
                        customerTableRow.Firstname = resultSet.getString("firstname");
                        customerTableRow.Lastname = resultSet.getString("lastname");
                        customerTableRow.Bonitaet = "Nicht eingetragen";

                        customerListModel.addElement(customerTableRow);
                    }
                }
            }

            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT * FROM `credit` WHERE status not LIKE ?;");
            stmtCredit.setString(1, CreditStatus.GENEHMIGT.toString());
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                while (resultSet.next()) {
                        CreditTableRow creditTableRow = new CreditTableRow();
                        creditTableRow.ID = resultSet.getInt("ID");
                        creditTableRow.CreditName = resultSet.getString("CreditName");
                        creditTableRow.CreditSum = resultSet.getString("CreditSum");
                        creditTableRow.TimeRange = resultSet.getString("CreditTimeRange");
                        creditTableRow.PaymentInterval = resultSet.getString("PaymentInterval");
                        creditTableRow.Status = resultSet.getString("Status");

                        if(checkSuggestions(dbConnection, creditTableRow.ID)){
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
                    if(total < 2){
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
