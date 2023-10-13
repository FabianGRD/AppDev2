package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame
{
    JFrame frame;
    JButton login, register, exit;
    JPanel panel;
    JTextField username, password;
    JLabel usernameLable, passwordLable;
    GridBagConstraints c = new GridBagConstraints();

    public Menu(){
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
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(750, 600));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        usernameLable = new JLabel("Username: ");
        panel.add(usernameLable, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        username = new JTextField(30);
        panel.add(username, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        passwordLable = new JLabel("Passwort: ");
        panel.add(passwordLable, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        password = new JPasswordField(30);
        panel.add(password, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        login = new JButton("Login");
        register = new JButton("Registrieren");
        exit = new JButton("Schließen");

        panel.add(login, c);
        panel.add(register, c);
        panel.add(exit, c);
        frame.add(panel);

        frame.revalidate();
        frame.repaint();

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
            AdminMenu adminMenu = new AdminMenu(frame);
            adminMenu.createMenuAdmin();
        } else if(username.getText().toLowerCase().equals("kunde"))
        {
            CustomerMenu customerMenu = new CustomerMenu(frame);
            customerMenu.createMenuCustomer();
        }else {
            createLogin();
        }
    }
}
