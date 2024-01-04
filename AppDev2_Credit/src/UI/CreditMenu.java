package UI;

import Backend.CreditStatus;
import Backend.CreditTimeRange;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditMenu extends JFrame{
    private JPanel panel;
    private JTextField creditSum;
    private JLabel CreditSum;
    private JComboBox timeRange;
    private JTextField interestRate;
    private JComboBox payTimeRange;
    private JTextField payAmountPerTimeSlot;
    private JButton acceptCredit;
    private JButton cancelButton;
    private JTextField purposeOfUse;

    public CreditMenu(Connection dbConnection, int customerId){
        setContentPane(panel);
        setVisible(true);
        setTitle("Customer Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        int intrestRateId = loadInitialValues(dbConnection);

        payTimeRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SetTimeRangeValues();
                CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new CustomerMenu(dbConnection, customerId);
            }
        });

        timeRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
            }
        });

        acceptCredit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveCredit(dbConnection, customerId, intrestRateId) ;
            }
        });

        creditSum.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
            }
        });
    }

    private void CalculateCreditRate(int timeRange, int creditSum) {
        double payAmount = creditSum/timeRange;
        String payAmountString = "" + payAmount;
        payAmountPerTimeSlot.setText(payAmountString);
    }

    private void SaveCredit(Connection dbConnection, int customerId, int intrestRateId) {

        try {
            dbConnection.setAutoCommit(false);
            PreparedStatement stmt = dbConnection.prepareStatement("INSERT INTO credit(CustomerId, CreditSum, CreditTimeRange, PaymentInterval, CreditName, InterestRateId, Status) VALUES (?,?,?,?,?,?,?) ", PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, customerId);
            stmt.setInt(2, Integer.parseInt(creditSum.getText()));
            stmt.setInt(3, Integer.parseInt(timeRange.getSelectedItem().toString()));
            stmt.setString(4, payTimeRange.getSelectedItem().toString());
            stmt.setString(5, purposeOfUse.getText());
            stmt.setInt(6, intrestRateId);
            stmt.setString(7, GetCreditStatus(dbConnection, customerId, Integer.parseInt(creditSum.getText())).toString());
            stmt.executeUpdate();
            dbConnection.commit();
        }catch(Exception e){
            System.out.println("Kredit konnte nicht gespeichert werden " + e);
            return;
        }

        setVisible(false);
        new CustomerMenu(dbConnection, customerId);
    }

    private CreditStatus GetCreditStatus( Connection dbConnection, int customerId, int creditSum ) throws SQLException {

        try {
            PreparedStatement stmt = dbConnection.prepareStatement("SELECT bonitaet FROM customer WHERE ID = ?;");
            stmt.setInt(1, customerId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String bonitaet = resultSet.getString("bonitaet");
                    if(bonitaet == null){
                        return CreditStatus.BONITAET_NICHT_ERFASST;
                    }else if(creditSum <= Integer.parseInt(bonitaet)){
                        return CreditStatus.GENEHMIGT;
                    }else{
                        return CreditStatus.OFFEN;
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Kredit konnte nicht gespeichert werden " + e);
            throw new SQLException("Bonität Fehler");
        }
        return CreditStatus.OFFEN;
    }

    private int loadInitialValues(Connection dbConnection)
    {
        String selectQuery = "SELECT * FROM initialcreditvalues Order by ID DESC Limit 1;";
        int intrestRateId = 0;
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            try(ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()) {
                    String interestRateFromDB = resultSet.getString("InterestRate");
                    intrestRateId = resultSet.getInt("ID");
                    interestRate.setText(interestRateFromDB);
                }else{
                    System.out.println("Kein Zinssatz gefunden");
                }
            }
        }catch (SQLException exception){
            System.out.println(exception);
        }

        payTimeRange.addItem(CreditTimeRange.MONTHLY);
        payTimeRange.addItem(CreditTimeRange.QUARTERLY);
        payTimeRange.addItem(CreditTimeRange.YEARLY);

        for (int i = 1; i <= 10; i++){
            int range = 12 * i;
            timeRange.addItem(range);
        }

        return intrestRateId;
    }

    private void SetTimeRangeValues(){
        if(CreditTimeRange.MONTHLY == payTimeRange.getSelectedItem()){
            timeRange.removeAllItems();
            for (int i = 1; i <= 10; i++){
                int range = 12 * i;
                timeRange.addItem(range);
            }
        }else if(CreditTimeRange.QUARTERLY == payTimeRange.getSelectedItem()){
            timeRange.removeAllItems();
            for (int i = 1; i <= 10; i++){
                int range = 4 * i;
                timeRange.addItem(range);
            }
        }else if(CreditTimeRange.YEARLY == payTimeRange.getSelectedItem()){
            timeRange.removeAllItems();
            for (int i = 1; i <= 10; i++){
                int range = 1 * i;
                timeRange.addItem(range);
            }
        }
    }
}
