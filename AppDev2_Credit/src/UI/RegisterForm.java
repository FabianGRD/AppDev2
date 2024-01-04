package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegisterForm extends JFrame{

    private JPanel panel;
    private JTextField username;
    private JPasswordField password;
    private JTextField firstname;
    private JTextField lastname;
    private JTextField street;
    private JTextField postalcode;
    private JTextField city;
    private JButton registerButton;
    private JButton returnButton;

    public RegisterForm(Connection dbConnection, JTextField usernameInput, JPasswordField passwordInput){
        setContentPane(panel);
        setVisible(true);
        setTitle("Registrieren");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        username.setText(usernameInput.getText());
        password.setText(passwordInput.getText());
        repaint();

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                registerCustomer(dbConnection);
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new Login(dbConnection);
            }
        });
    }

    private void registerCustomer(Connection dbConnection) {
        if (username.getText() != "" &&
            firstname.getText() != "" &&
            lastname.getText() != "" &&
            street.getText() != "" &&
            postalcode.getText() != "" &&
            city.getText() != "" &&
            password.getText() != "") {
            try{
                dbConnection.setAutoCommit(false);

                Statement statement = dbConnection.createStatement();

                PreparedStatement stmt = dbConnection.prepareStatement("INSERT INTO customer (firstname, lastname, street, postalcode, city) VALUES(?,?,?,?,?) ", PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, firstname.getText());
                stmt.setString(2, lastname.getText());
                stmt.setString(3, street.getText());
                stmt.setInt(4, Integer.parseInt(postalcode.getText()));
                stmt.setString(5, city.getText());
                stmt.executeUpdate();

                int generatedKey = 0;
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedKey  = generatedKeys.getInt(1);
                    }
                }catch (Exception e){
                    return;
                }

                PreparedStatement stmtLogin = dbConnection.prepareStatement("INSERT INTO login (username, password, customer) VALUES (?, ?, ?)");
                stmtLogin.setString(1, username.getText());
                stmtLogin.setString(2, password.getText());
                stmtLogin.setInt(3, generatedKey);
                stmtLogin.executeUpdate();

                dbConnection.commit();
            }catch(SQLException exception){
                System.out.println(exception);
            }
        }else {
            System.out.println("Sie müssen alle Felder ausfüllen");
        }

        setVisible(false);
        new Login(dbConnection);
    }
}
