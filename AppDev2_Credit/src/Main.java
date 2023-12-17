import Database.DBConnection;
import UI.Login;
import java.sql.*;
import UI.Menu_alt;

public class Main {
    public static void main( String[] args )
    {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.connectToDB();
        Login login = new Login(connection);
    }
}
