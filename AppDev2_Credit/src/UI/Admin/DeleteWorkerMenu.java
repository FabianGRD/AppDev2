package UI.Admin;

import Backend.WorkerBase;
import UI.Admin.AdminMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteWorkerMenu extends JFrame {
    private JPanel panel;
    private JComboBox allWorkers;
    private JButton deleteWorker;
    private JButton cancelButton;

    public DeleteWorkerMenu( Connection dbConnection, int workerId) {
        setContentPane(panel);
        setVisible(true);
        setTitle("Delete User Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadInitialValues(dbConnection, workerId);

        deleteWorker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                deleteSelectedWorker(dbConnection, workerId);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
                setVisible(false);
                new AdminMenu(dbConnection, workerId);
            }
        });
    }

    private void loadInitialValues( Connection dbConnection, int workerId ) {
        String selectQuery = "SELECT * FROM worker WHERE ID != ?";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(selectQuery);
            stmt.setInt(1, workerId);
            try(ResultSet resultSet = stmt.executeQuery()){
                while (resultSet.next()) {
                    WorkerBase workerBase = new WorkerBase();
                    workerBase.id = resultSet.getInt("ID");
                    workerBase.firstname  = resultSet.getString("firstname");
                    workerBase.lastname  = resultSet.getString("lastname");
                    allWorkers.addItem(workerBase);
                }
            }
        }catch (SQLException exception){
            System.out.println(exception);
        }
    }

    private void deleteSelectedWorker(Connection dbConnection, int workerId){
        WorkerBase workerBase = (WorkerBase) allWorkers.getSelectedItem();
        int selectedIndex = allWorkers.getSelectedIndex();
        String deleteLogin = "DELETE FROM login WHERE worker = (?)";
        String deleteWorker = "DELETE FROM worker WHERE id = (?)";
        try {
            dbConnection.setAutoCommit(false);
            PreparedStatement stmtDeleteLogin = dbConnection.prepareStatement(deleteLogin);
            stmtDeleteLogin.setInt(1, workerBase.id);
            stmtDeleteLogin.executeUpdate();

            PreparedStatement stmtDeleteWorker = dbConnection.prepareStatement(deleteWorker);
            stmtDeleteWorker.setInt(1, workerBase.id);
            stmtDeleteWorker.executeUpdate();

            dbConnection.commit();

            allWorkers.removeAllItems();
            loadInitialValues(dbConnection, workerId);
        }catch (SQLException exception){
            System.out.println(exception);
        }
    }
}
