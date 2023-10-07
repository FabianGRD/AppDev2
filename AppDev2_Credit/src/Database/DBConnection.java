package Database;
import java.sql.*;

public class DBConnection {
    static Connection dbConnection = null;
    private String url = "jdbc:mysql://localhost:3306/AppDev2"; // table details
    private String username = "root"; // MySQL credentials
    private String password = "";
    private Connection con;

    public DBConnection() {
        System.out.println("Connecting database...");

        try {
            Connection connect = DriverManager.getConnection(url, username, password);
            con = connect;
            System.out.println("Database connected!");

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }
}
