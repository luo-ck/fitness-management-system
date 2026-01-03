import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseChecker {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/fitness?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        String username = "root";
        String password = "313813Shx@";
        String tableName = "users";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

                System.out.println("Table " + tableName + " columns:");
                System.out.println("------------------------------------------------------");
                System.out.printf("%s\t%s\t%s\t%s\t%s\n", "Name", "Type", "Size", "Nullable", "Default");
                System.out.println("------------------------------------------------------");

                boolean hasUsername = false;
                boolean hasName = false;

                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    String columnType = resultSet.getString("TYPE_NAME");
                    int columnSize = resultSet.getInt("COLUMN_SIZE");
                    String isNullable = resultSet.getString("IS_NULLABLE");
                    String columnDef = resultSet.getString("COLUMN_DEF");

                    System.out.printf("%s\t%s\t%d\t%s\t%s\n", columnName, columnType, columnSize, isNullable, columnDef != null ? columnDef : "-");

                    if ("username".equals(columnName)) {
                        hasUsername = true;
                    }
                    if ("name".equals(columnName)) {
                        hasName = true;
                    }
                }

                System.out.println("------------------------------------------------------");
                System.out.println("Check results:");
                System.out.println("- Has username column: " + (hasUsername ? "Yes" : "No"));
                System.out.println("- Has name column: " + (hasName ? "Yes" : "No"));

                if (hasUsername && hasName) {
                    System.out.println("Warning: Both username and name columns exist! Need to delete username.");
                } else if (!hasName) {
                    System.out.println("Error: Missing name column!");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}