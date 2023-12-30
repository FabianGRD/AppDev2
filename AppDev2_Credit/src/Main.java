import Database.DBConnection;
import UI.Login;

import java.sql.*;

public class Main {
    public static void main( String[] args )
    {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.connectToDB();
        new Login(connection);
    }
}
