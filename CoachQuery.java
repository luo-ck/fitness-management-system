import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CoachQuery {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/fitness?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "123456";

        try {
            // Load MySQL driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM coaches")) {

                System.out.println("Coaches table data:");
                while (rs.next()) {
                    long coachId = rs.getLong("coach_id");
                    String username = rs.getString("username");
                    String name = rs.getString("name");
                    boolean isVerified = rs.getBoolean("is_verified");
                    boolean isDeleted = rs.getBoolean("is_deleted");
                    System.out.printf("ID: %d, Username: %s, Name: %s, Verified: %b, Deleted: %b%n", 
                                    coachId, username, name, isVerified, isDeleted);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}