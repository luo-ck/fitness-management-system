import java.sql.*;

public class UserQuery {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/fitness?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        String username = "root";
        String password = "313813Shx@";
        
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database
            Connection conn = DriverManager.getConnection(url, username, password);
            
            // Create SQL query
            String sql = "SELECT user_id, name, age, gender, created_at FROM users WHERE is_deleted = 0";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            // Print header
            System.out.printf("%-10s %-20s %-10s %-10s %-20s%n", "User ID", "Username", "Age", "Gender", "Created At");
            System.out.println("-------------------------------------------------------------------");
            
            // Print each user
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                Integer age = rs.getObject("age") != null ? rs.getInt("age") : null;
                String gender = rs.getString("gender");
                Timestamp createdAt = rs.getTimestamp("created_at");
                
                System.out.printf("%-10d %-20s %-10s %-10s %-20s%n", userId, name, age != null ? age : "N/A", gender != null ? gender : "N/A", createdAt);
            }
            
            // Close resources
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
    }
}