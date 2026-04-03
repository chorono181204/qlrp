package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class JdbcUtil {
    private static final String URL = AppConfig.getRequired("db.url");
    private static final String USERNAME = AppConfig.getRequired("db.username");
    private static final String PASSWORD = AppConfig.getRequired("db.password");

    private JdbcUtil() {

    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong connection: " + e.getMessage());
            }
        }
    }
}
