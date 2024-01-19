package UI.Customer;

import Backend.CreditStatus;
import Backend.CreditTimeRange;
import UI.Customer.CustomerMenu;

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
        setTitle("Credit Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        int intrestRateId = loadInitialValues(dbConnection);

        payTimeRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SetTimeRangeValues();
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
                try{
                   CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
                } catch (Exception exception){
                   System.out.println(exception);
                }
            }
        });

        acceptCredit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveCredit(dbConnection, customerId, intrestRateId) ;
            }
        });

        creditSum.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkInputValue();
                try{
                    CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
                } catch (Exception exception){
                    System.out.println(exception);
                }
            }
        });
    }

    private void checkInputValue() {
        String fullString = creditSum.getText();
        String lastChar = fullString.substring(fullString.length() - 1);
        String numbers = "0123456789";
        if(!numbers.contains(lastChar)){
            fullString = fullString.substring(0, fullString.length() - 1);
            creditSum.setText(fullString);
        }
    }

    private void CalculateCreditRate(int timeRangeValue, int creditSumValue) {
        String fullInput = creditSum.getText();
        double intrestRateValue = Double.parseDouble(interestRate.getText().toString());
        int months = 0;
        double years = 0;
        double totalSum = creditSumValue;

        switch ((CreditTimeRange)payTimeRange.getSelectedItem()) {
            case MONTHLY:
                years = timeRangeValue / 12;
                intrestRateValue = (intrestRateValue + 100) / 100;
                totalSum = creditSumValue * Math.pow(intrestRateValue, years);
                break;
            case QUARTERLY:
                months  = timeRangeValue * 3;
                years = months / 12;
                intrestRateValue = (intrestRateValue + 100) / 100;
                totalSum = creditSumValue * Math.pow(intrestRateValue, years);
                break;
            case YEARLY:
                intrestRateValue = (intrestRateValue + 100) / 100;
                totalSum = creditSumValue * Math.pow(intrestRateValue, timeRangeValue);
                break;
            default:
                break;
        }
        if(fullInput.matches(".*[^a-z].*")) {
            double payAmount = totalSum / timeRangeValue;
            double d = Math.pow(10, 2);
            payAmount = Math.round(payAmount * d) / d;
            String payAmountString = "" + payAmount;
            payAmountPerTimeSlot.setText(payAmountString);
        }
    }

    private void SaveCredit(Connection dbConnection, int customerId, int intrestRateId) {

        try {
            dbConnection.setAutoCommit(false);
            PreparedStatement stmt = dbConnection.prepareStatement("INSERT INTO credit(CustomerId, CreditSum, CreditTimeRange, PaymentInterval, CreditName, InterestRateId, Status, suggestion) VALUES (?,?,?,?,?,?,?,?) ", PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, customerId);
            stmt.setInt(2, Integer.parseInt(creditSum.getText()));
            stmt.setInt(3, Integer.parseInt(timeRange.getSelectedItem().toString()));
            stmt.setString(4, payTimeRange.getSelectedItem().toString());
            stmt.setString(5, purposeOfUse.getText());
            stmt.setInt(6, intrestRateId);
            stmt.setString(7, GetCreditStatus(dbConnection, customerId, Integer.parseInt(creditSum.getText())).toString());
            stmt.setBoolean(8, false);
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

        timeRange.setSelectedIndex(0);
        try{
            CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
        } catch (Exception exception){
            System.out.println(exception);
        }
    }
}
