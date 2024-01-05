package UI;

import Backend.WorkerPermission;
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
                checkLogin(dbConnection);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        String selectQuery = "SELECT * FROM login WHERE username = ?";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            stmt.setString(1, username.getText());

            try(ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()) {
                    String selectedPassword = resultSet.getString("password");
                    String selectedCustomerId = resultSet.getString("customer");
                    String selectedWorkerId = resultSet.getString("worker");
                    if(password.getText().equals(selectedPassword)){
                        if(selectedCustomerId != null) {
                            setVisible(false);
                            new CustomerMenu(dbConnection, Integer.parseInt(selectedCustomerId));
                        }else if(selectedWorkerId != null){
                            GetWorkerPermission(dbConnection, Integer.parseInt(selectedWorkerId));
                        }
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
    private void GetWorkerPermission(Connection dbConnection, int workerId) {
        try {
            String selectQuery = "SELECT * FROM worker WHERE ID = ?";
            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            stmt.setInt(1, workerId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                        WorkerPermission actualPermission = WorkerPermission.valueOf(resultSet.getString("permission"));
                        switch (actualPermission) {
                            case WORKER:
                                setVisible(false);
                                new WorkerMenu(dbConnection, workerId);
                                break;
                            case ADMIN:
                                setVisible(false);
                                new AdminMenu(dbConnection, workerId);
                                break;
                            case SUPERIOR:
                                setVisible(false);
                                new SuperiorMenu(dbConnection, workerId);
                                break;
                            case MANAGER:
                                setVisible(false);
                                new ManagerMenu(dbConnection, workerId);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
    }
}
