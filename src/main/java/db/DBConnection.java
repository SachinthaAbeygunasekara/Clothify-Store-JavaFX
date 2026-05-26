package db;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class DBConnection {

    private Connection connection;
    private static DBConnection dbConnection;

    private DBConnection() throws SQLException {
        final String url = "jdbc:mysql://localhost:3306/clothify";
        final String username = "root";
        final String password = "1234";
        connection = DriverManager.getConnection(url, username, password);
    }

    public static DBConnection getInstance() throws SQLException {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }
}
