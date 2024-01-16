package Database;
import java.sql.*;

public class DBConnection {
    public String url = "jdbc:mysql://localhost:3306/appdev2_kreditanfrage"; // table details
    public String username = "root"; // MySQL credentials
    public String password = "";
    public Connection con;

    public Connection connectToDB() {
        System.out.println("Connecting database...");

        try {
            Connection connect = DriverManager.getConnection(url, username, password);
            con = connect;
            System.out.println("Database connected!");

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

        return con;
    }
}
