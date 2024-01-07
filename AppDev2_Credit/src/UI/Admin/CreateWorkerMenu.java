package UI.Admin;

import Backend.WorkerPermission;
import UI.Admin.AdminMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CreateWorkerMenu extends JFrame{
    private JTextField firstname;
    private JTextField lastname;
    private JComboBox permissionBox;
    private JButton createWorker;
    private JButton cancelButton;
    private JPanel panel;
    private JTextField username;
    private JTextField password;

    public CreateWorkerMenu( Connection dbConnection, int workerId) {
        setContentPane(panel);
        setVisible(true);
        setTitle("Admin Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadInitialValues(dbConnection);


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new AdminMenu(dbConnection, workerId);
            }
        });

        createWorker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveNewWorker(dbConnection, workerId) ;
            }
        });
    }

    private void loadInitialValues( Connection dbConnection ) {
        permissionBox.addItem(WorkerPermission.WORKER);
        permissionBox.addItem(WorkerPermission.MANAGER);
        permissionBox.addItem(WorkerPermission.SUPERIOR);
        permissionBox.addItem(WorkerPermission.ADMIN);
    }

    private void saveNewWorker( Connection dbConnection, int workerId ) {
        if (username.getText() != "" &&
                firstname.getText() != "" &&
                lastname.getText() != "" &&
                password.getText() != "") {
            try{
                dbConnection.setAutoCommit(false);

                PreparedStatement stmt = dbConnection.prepareStatement("INSERT INTO worker (firstname, lastname, permission) VALUES(?,?,?) ", PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, firstname.getText());
                stmt.setString(2, lastname.getText());
                stmt.setString(3, permissionBox.getSelectedItem().toString());
                stmt.executeUpdate();

                int generatedKey = 0;
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedKey  = generatedKeys.getInt(1);
                    }
                }catch (Exception e){
                    return;
                }

                PreparedStatement stmtLogin = dbConnection.prepareStatement("INSERT INTO login (username, password, worker) VALUES (?, ?, ?)");
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
        new AdminMenu(dbConnection, workerId);
    }
}
