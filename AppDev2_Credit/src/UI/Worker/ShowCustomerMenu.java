package UI.Worker;

import Backend.*;
import UI.Worker.WorkerMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowCustomerMenu extends JFrame {
    private JPanel panel;
    private JComboBox<CustomerBase> allCustomers;
    private JButton returnButton;
    private JButton acceptButton;
    private JTextField creditworthinessInput;

    public ShowCustomerMenu( Connection dbConnection, int workerId) {
        setContentPane(panel);
        setVisible(true);

        setTitle("Bonitaet Menu");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadInitialValues(dbConnection, workerId);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new WorkerMenu(dbConnection, workerId);
            }
        });

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setCreditworthiness(dbConnection, workerId);
            }
        });
    }

    private void calculateCustomerCreditsNew( Connection dbConnection ) {
        String selectQuery = "SELECT * FROM credit WHERE CustomerId = ?";
        CustomerBase customerBase = (CustomerBase) allCustomers.getSelectedItem();
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            stmt.setInt(1, customerBase.id);
            try(ResultSet resultSet = stmt.executeQuery()){
                while (resultSet.next()) {
                    int creditId = resultSet.getInt("ID");
                    int creditSum = resultSet.getInt("CreditSum");
                    setCustomerCreditStatus(dbConnection, creditId, creditSum);
                }
            }
        }catch (SQLException exception){
            System.out.println(exception);
        }
    }

    private void setCustomerCreditStatus( Connection dbConnection, int creditId, int creditSum ) {
        try {
            dbConnection.setAutoCommit(false);

            String insertQuery = "UPDATE credit SET Status = ? WHERE ID = ?";
            PreparedStatement stmt = dbConnection.prepareStatement(insertQuery);

            if(creditSum <=  Integer.parseInt(creditworthinessInput.getText())){
                stmt.setString(1, CreditStatus.GENEHMIGT.toString());
            }else{
                stmt.setString(1, CreditStatus.OFFEN.toString());
            }
            stmt.setInt(2, creditId);

            stmt.executeUpdate();
            dbConnection.commit();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void setCreditworthiness( Connection dbConnection, int workerId ) {
        CustomerBase customerBase = (CustomerBase) allCustomers.getSelectedItem();
        try {
            dbConnection.setAutoCommit(false);

            String insertQuery = "UPDATE customer SET bonitaet = ? WHERE ID = ?;";
            PreparedStatement stmt = dbConnection.prepareStatement(insertQuery);
            stmt.setString(1, creditworthinessInput.getText());
            stmt.setInt(2, customerBase.id);

            stmt.executeUpdate();
            dbConnection.commit();
        }catch(Exception e){
            System.out.println(e);
        }

        calculateCustomerCreditsNew(dbConnection);
        allCustomers.removeAllItems();
        loadInitialValues(dbConnection, workerId);
    }

    private void loadInitialValues( Connection dbConnection, int workerId) {
        try {
            String selectQuery = "SELECT * FROM customer where bonitaet is null";

            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            try(ResultSet resultSet = stmt.executeQuery()){
                while (resultSet.next()) {
                    CustomerBase customerBase = new CustomerBase();
                    customerBase.id = resultSet.getInt("ID");
                    customerBase.firstname  = resultSet.getString("firstname");
                    customerBase.lastname  = resultSet.getString("lastname");
                    allCustomers.addItem(customerBase);
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }

        if(allCustomers.getSelectedItem() == null){
            setVisible(false);
            new WorkerMenu(dbConnection, workerId);
        }
    }
}
