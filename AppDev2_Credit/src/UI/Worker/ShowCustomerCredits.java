package UI.Worker;

import Backend.*;
import UI.Worker.WorkerMenu;

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
        loadInitialValues(dbConnection, workerId);

        payTimeRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                SetTimeRangeValues();
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
                try{
                    CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
                } catch (Exception exception){
                    System.out.println(exception);
                }
            }
        });

        creditSuggestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCreditSuggestion(dbConnection, workerId) ;
            }
        });

        allCredits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCreditValues(dbConnection, workerId);
                try{
                    CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
                } catch (Exception exception){
                    System.out.println(exception);
                }
            }
        });

        creditSum.addKeyListener(new KeyAdapter() {
            public void keyReleased( KeyEvent e) {
                try{
                    CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
                } catch (Exception exception){
                    System.out.println(exception);
                }
            }
        });
    }

    private void saveCreditSuggestion( Connection dbConnection, int workerId ) {
        CreditBase creditBase = (CreditBase) allCredits.getSelectedItem();

        try {
            dbConnection.setAutoCommit(false);
            PreparedStatement stmt = dbConnection.prepareStatement("INSERT INTO credit(CustomerId, CreditSum, CreditTimeRange, PaymentInterval, CreditName, InterestRateId, Status, suggestion, originId, firstSuggestionWorkerID) VALUES (?,?,?,?,?,?,?,?,?,?) ", PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, creditBase.CustomerId );
            stmt.setInt(2, Integer.parseInt(creditSum.getText()));
            stmt.setInt(3, Integer.parseInt(timeRange.getSelectedItem().toString()));
            stmt.setString(4, payTimeRange.getSelectedItem().toString());
            stmt.setString(5, purposeOfUse.getText());
            stmt.setInt(6, creditBase.InterestRateId);
            stmt.setString(7, CreditStatus.BEARBEITUNG.toString());
            stmt.setBoolean(8, true);
            stmt.setInt(9, creditBase.Id);
            stmt.setInt(10, workerId);
            stmt.executeUpdate();

            if(creditBase.FirstSuggestion != null){
                PreparedStatement updateStmt = dbConnection.prepareStatement("UPDATE credit SET secondSuggestionWorkerID = ? Where ID = ? ;");
                updateStmt.setInt(1, workerId);
                updateStmt.setInt(2, creditBase.Id);
                updateStmt.executeUpdate();
            }else {
                PreparedStatement updateStmt = dbConnection.prepareStatement("UPDATE credit SET firstSuggestionWorkerID = ? Where ID = ? ;");
                updateStmt.setInt(1, workerId);
                updateStmt.setInt(2, creditBase.Id);
                updateStmt.executeUpdate();
            }


            dbConnection.commit();

            allCredits.removeAllItems();
            loadInitialValues(dbConnection, workerId);
        }catch(Exception e){
            System.out.println("Kredit konnte nicht gespeichert werden " + e);
        }
    }

    private void selectCreditValues( Connection dbConnection, int workerId) {
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

    private void loadInitialValues( Connection dbConnection, int workerId) {

        payTimeRange.addItem(CreditTimeRange.MONTHLY);
        payTimeRange.addItem(CreditTimeRange.QUARTERLY);
        payTimeRange.addItem(CreditTimeRange.YEARLY);

        for (int i = 1; i <= 10; i++){
            int range = 12 * i;
            timeRange.addItem(range);
        }

        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT ID, CreditName, CreditSum, CustomerId, InterestRateId, firstSuggestionWorkerID FROM `credit` WHERE status not LIKE ? AND suggestion = ? AND (firstSuggestionWorkerID != ? OR firstSuggestionWorkerID is null)");
            stmtCredit.setString(1, CreditStatus.GENEHMIGT.toString());
            stmtCredit.setBoolean(2, false);
            stmtCredit.setInt(3, workerId);

            try (ResultSet resultSet = stmtCredit.executeQuery()) {
                while (resultSet.next()) {
                    CreditBase creditBase = new CreditBase();
                    creditBase.Id = resultSet.getInt("ID");
                    creditBase.Name = resultSet.getString("CreditName");
                    creditBase.CreditSum = resultSet.getInt("CreditSum");
                    creditBase.CustomerId = resultSet.getInt("CustomerId");
                    creditBase.InterestRateId = resultSet.getInt("InterestRateId");
                    creditBase.FirstSuggestion = resultSet.getString("firstSuggestionWorkerID");

                    if(checkSuggestions(dbConnection, creditBase.Id)){
                        allCredits.addItem(creditBase);
                    }
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }
        if(allCredits.getSelectedItem() == null){
            setVisible(false);
            new WorkerMenu(dbConnection, workerId);
        }else{
            selectCreditValues(dbConnection, workerId);
        }
    }

    private boolean checkSuggestions( Connection dbConnection, int originId ) {
        try {
            PreparedStatement stmtCredit = dbConnection.prepareStatement("SELECT Count(ID) as total FROM `credit` WHERE originId = ?;");
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

        try{
            CalculateCreditRate(Integer.parseInt(timeRange.getSelectedItem().toString()), Integer.parseInt(creditSum.getText()));
        } catch (Exception exception){
            System.out.println(exception);
        }
    }

    private void CalculateCreditRate(int timeRange, int creditSum) {
        double payAmount = creditSum/timeRange;
        String payAmountString = "" + payAmount;
        payAmountPerTimeSlot.setText(payAmountString);
    }
}
