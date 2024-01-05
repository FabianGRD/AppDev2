package UI;

import Backend.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowCustomerCredits extends JFrame{
    private JPanel panel;
    private JTextField purposeOfUse;
    private JTextField creditSum;
    private JComboBox timeRange;
    private JComboBox payTimeRange;
    private JTextField payAmountPerTimeSlot;
    private JButton cancelButton;
    private JButton creditSuggestionButton;
    private JComboBox<CreditBase> allCredits;
    private JTextField interestRate;

    public ShowCustomerCredits(Connection dbConnection, int workerId){
        setContentPane(panel);
        setVisible(true);
        setTitle("Customer Credits Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadInitialValues(dbConnection);

        payTimeRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                SetTimeRangeValues();
                CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new WorkerMenu(dbConnection, workerId);
            }
        });

        timeRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
            }
        });

        creditSuggestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCreditSuggestion(dbConnection) ;
            }
        });

        allCredits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCreditValues(dbConnection);
                CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
            }
        });

        creditSum.addKeyListener(new KeyAdapter() {
            public void keyReleased( KeyEvent e) {
                CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
            }
        });
    }

    private void saveCreditSuggestion( Connection dbConnection ) {
        CreditBase creditBase = (CreditBase) allCredits.getSelectedItem();

        try {
            dbConnection.setAutoCommit(false);
            PreparedStatement stmt = dbConnection.prepareStatement("INSERT INTO credit(CustomerId, CreditSum, CreditTimeRange, PaymentInterval, CreditName, InterestRateId, Status, suggestion, originId) VALUES (?,?,?,?,?,?,?,?,?) ", PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, creditBase.CustomerId );
            stmt.setInt(2, Integer.parseInt(creditSum.getText()));
            stmt.setInt(3, Integer.parseInt(timeRange.getSelectedItem().toString()));
            stmt.setString(4, payTimeRange.getSelectedItem().toString());
            stmt.setString(5, purposeOfUse.getText());
            stmt.setInt(6, creditBase.InterestRateId);
            stmt.setString(7, CreditStatus.BEARBEITUNG.toString());
            stmt.setBoolean(8, true);
            stmt.setInt(9, creditBase.Id);
            stmt.executeUpdate();
            dbConnection.commit();
        }catch(Exception e){
            System.out.println("Kredit konnte nicht gespeichert werden " + e);
        }
    }

    private void selectCreditValues( Connection dbConnection ) {
        CreditBase selectedCreditBase = (CreditBase) allCredits.getSelectedItem();
        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT * FROM `credit` WHERE ID = ?;");
            stmtCredit.setInt(1, selectedCreditBase.Id);
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                if (resultSet.next()) {
                    purposeOfUse.setText(resultSet.getString("CreditName"));
                    creditSum.setText(resultSet.getString("CreditSum"));
                    timeRange.setSelectedItem(resultSet.getInt("CreditTimeRange"));
                    payTimeRange.setSelectedItem(resultSet.getString("PaymentInterval"));
                    int interestRateId = resultSet.getInt("InterestRateId");
                    interestRate.setText(getInterestRate(dbConnection, interestRateId));
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    private String getInterestRate( Connection dbConnection, int interestRateId ) {
        String selectQuery = "SELECT * FROM initialcreditvalues WHERE ID = ?";
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

    private void loadInitialValues( Connection dbConnection ) {

        payTimeRange.addItem(CreditTimeRange.MONTHLY);
        payTimeRange.addItem(CreditTimeRange.QUARTERLY);
        payTimeRange.addItem(CreditTimeRange.YEARLY);

        for (int i = 1; i <= 10; i++){
            int range = 12 * i;
            timeRange.addItem(range);
        }

        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT ID, CreditName, CreditSum, CustomerId, InterestRateId FROM `credit` WHERE status not LIKE ?;");
            stmtCredit.setString(1, CreditStatus.GENEHMIGT.toString());
            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                while (resultSet.next()) {
                    CreditBase creditBase = new CreditBase();
                    creditBase.Id = resultSet.getInt("ID");
                    creditBase.Name = resultSet.getString("CreditName");
                    creditBase.CreditSum = resultSet.getInt("CreditSum");
                    creditBase.CustomerId = resultSet.getInt("CustomerId");
                    creditBase.InterestRateId = resultSet.getInt("InterestRateId");

                    if(checkSuggestions(dbConnection, creditBase.Id)){
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

    private void CalculateCreditRate(int timeRange, int creditSum) {
        double payAmount = creditSum/timeRange;
        String payAmountString = "" + payAmount;
        payAmountPerTimeSlot.setText(payAmountString);
    }
}
