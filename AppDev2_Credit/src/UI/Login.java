package UI;

import Database.DBConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class Login extends JFrame{
    private JPanel panel;
    private JPasswordField password;
    private JTextField username;
    private JButton loginButton;
    private JButton exitButton;
    private JButton registerButton;

    public Login(Connection dbConnection){
        setContentPane(panel);
        setVisible(true);
        setTitle("Login");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //String selectedUserType = (String) userTypeComboBox.getSelectedItem();
                checkLogin(dbConnection);
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //String selectedUserType = (String) userTypeComboBox.getSelectedItem();
                registerCustomer(dbConnection);
            }
        });
    }

    private void registerCustomer(Connection dbConnection) {
        setVisible(false);
        new RegisterForm(dbConnection, username, password);
    }

    private void checkLogin(Connection dbConnection)
    {
        if(username.getText().toLowerCase().equals("admin"))
        {
            setVisible(false);
            new AdminMenu();
        } else
        {
            String selectQuery = "SELECT * FROM login WHERE username = ?";
            try {
                PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
                stmt.setString(1, username.getText());
                try(ResultSet resultSet = stmt.executeQuery()){
                    if (resultSet.next()) {
                        String selectedPassword = resultSet.getString("password");
                        if(password.getText().equals(selectedPassword)){
                            new CustomerMenu();
                        }
                    }else{
                        username.setText("");
                        password.setText("");
                        System.out.println("Kein user gefunden!");
                    }
                }
            }catch (SQLException exception){
                System.out.println(exception);
            }
        }
    }
}
