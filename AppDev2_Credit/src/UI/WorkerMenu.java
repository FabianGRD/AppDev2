package UI;

import Backend.CreditStatus;
import Backend.CreditTableRow;
import Backend.CustomerTableRow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WorkerMenu extends JFrame {
    private JPanel panel;
    private JButton logoutButton;
    private JButton showAllCredits;
    private JList<CustomerTableRow> customerList;
    private JList<CreditTableRow>  creditList;
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
    }

    private void loadInitialValues( Connection dbConnection ) {
        try {
            PreparedStatement stmtCustomer = dbConnection.prepareStatement("SELECT * FROM customer;");
            try (ResultSet resultSet = stmtCustomer.executeQuery()) {
                while (resultSet.next()) {
                    if(resultSet.getString("bonitaet") == null){
                        CustomerTableRow customerTableRow = new CustomerTableRow();
                        customerTableRow.ID = resultSet.getInt("ID");
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
                        creditTableRow.CreditName = resultSet.getString("CreditName");
                        creditTableRow.CreditSum = resultSet.getString("CreditSum");
                        creditTableRow.TimeRange = resultSet.getString("CreditTimeRange");
                        creditTableRow.PaymentInterval = resultSet.getString("PaymentInterval");
                        creditTableRow.Status = resultSet.getString("Status");

                        creditListModel.addElement(creditTableRow);
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }
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
                        ", Interval: " + credit.PaymentInterval+
                        ", Zins: " + credit.InterestRate+
                        ", Status: " + credit.Status);
            }

            return this;
        }
    }

    private class CustomerListRenderer extends DefaultListCellRenderer {
        public java.awt.Component getListCellRendererComponent(JList list,
                                                               Object value,
                                                               int index,
                                                               boolean isSelected,
                                                               boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof CustomerListRenderer) {
                CustomerTableRow customer = (CustomerTableRow) value;
                setText("ID: " + customer.ID +
                        ", Firstname: " + customer.Firstname+
                        ", Lastname: " + customer.Lastname+
                        ", Bonitaet: " + customer.Bonitaet);
            }

            return this;
        }
    }
}
