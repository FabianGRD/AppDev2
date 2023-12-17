package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu_alt extends JFrame
{
    JFrame frame;
    JButton login, register, exit;
    JPanel panel;
    JTextField username, password;
    JLabel usernameLable, passwordLable;
    GridBagConstraints c = new GridBagConstraints();

    public Menu_alt(){
        createFrame();
    }

    public void createFrame(){
        frame = new JFrame();
        frame.setSize(800,700);
        frame.setVisible(true);
        frame.setLayout(new GridBagLayout());
        frame.setBackground(Color.black);

    }

    public void createLogin()
    {

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(panel);
                frame.revalidate();
                frame.repaint();
                checkLogin();
            }
        });
    }

    private void checkLogin()
    {
        if(username.getText().toLowerCase().equals("admin"))
        {
            AdminMenu_Alt adminMenuAlt = new AdminMenu_Alt(frame);
            adminMenuAlt.createMenuAdmin();
        } else if(username.getText().toLowerCase().equals("kunde"))
        {
            CustomerMenu_alt customerMenuAlt = new CustomerMenu_alt(frame);
            customerMenuAlt.createMenuCustomer();
        }else {
            createLogin();
        }
    }
}
