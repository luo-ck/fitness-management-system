import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUpdater {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/fitness?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        String username = "root";
        String password = "313813Shx@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 Statement statement = connection.createStatement()) {

                // Drop username column
                String sql = "ALTER TABLE users DROP COLUMN username";
                int rowsAffected = statement.executeUpdate(sql);
                System.out.println("Successfully dropped username column. Rows affected: " + rowsAffected);

                // Check table structure after modification
                System.out.println("\nTable structure after modification:");
                statement.execute("DESCRIBE users");
                var resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\n",
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6));
                }

            } catch (SQLException e) {
                if (e.getErrorCode() == 1091) {
                    System.out.println("Error: username column does not exist.");
                } else {
                    System.err.println("Database error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
        }
    }
}