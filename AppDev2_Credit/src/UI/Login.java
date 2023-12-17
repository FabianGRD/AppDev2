package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame{
    private JPanel panel;
    private JPasswordField passwordField1;
    private JTextField username;
    private JButton loginButton;
    private JButton exitButton;

    public Login(){
        setContentPane(panel);
        setVisible(true);
        setTitle("Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //String selectedUserType = (String) userTypeComboBox.getSelectedItem();
                checkLogin();
            }
        });
    }

    private void checkLogin()
    {
        if(username.getText().toLowerCase().equals("admin"))
        {
            setVisible(false);
            new AdminMenu();
        } else if(username.getText().toLowerCase().equals("kunde"))
        {
            setVisible(false);
            new CustomerMenu();
        }else {
            passwordField1.setText("");
            username.setText("");
        }
    }
}
