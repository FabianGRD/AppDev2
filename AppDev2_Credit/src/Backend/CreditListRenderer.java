package Backend;

import javax.swing.*;

public class CreditListRenderer extends DefaultListCellRenderer {
    public java.awt.Component getListCellRendererComponent(JList list,
                                                           Object value,
                                                           int index,
                                                           boolean isSelected,
                                                           boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof CreditTableRow) {
            CreditTableRow credit = (CreditTableRow) value;
            setText("ID: " + credit.ID +
                    ", Credit: " + credit.CreditName +
                    ", Summe: " + credit.CreditSum+
                    ", Zeitspanne: " + credit.TimeRange+
                    ", Interval: " + credit.PaymentInterval+
                    ", Zins: " + credit.InterestRate+
                    ", Status: " + credit.Status);
        }

        return this;
    }
}