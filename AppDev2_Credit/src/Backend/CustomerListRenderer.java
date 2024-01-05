package Backend;

import javax.swing.*;

public class CustomerListRenderer extends DefaultListCellRenderer {
    public java.awt.Component getListCellRendererComponent(JList list,
                                                           Object value,
                                                           int index,
                                                           boolean isSelected,
                                                           boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof CustomerTableRow) {
            CustomerTableRow customer = (CustomerTableRow) value;
            setText("ID: " + customer.ID +
                    ", Firstname: " + customer.Firstname+
                    ", Lastname: " + customer.Lastname+
                    ", Bonitaet: " + customer.Bonitaet);
        }

        return this;
    }
}