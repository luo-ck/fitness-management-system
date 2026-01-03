import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateCoachPassword {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/fitness?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        String username = "root";
        String password = "313813Shx@";
        String coachUsername = "老牛";
        String newPasswordHash = "$2a$10$x/6RdwZqPDaHaZyu5xA7jOVoweVAu8pMNv7DBp2ri7ld3b7UeVpwi";

        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 建立连接
            Connection conn = DriverManager.getConnection(url, username, password);
            // 创建SQL语句
            String sql = "UPDATE coaches SET password_hash = ? WHERE username = ? AND is_deleted = 0";
            // 创建PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, coachUsername);
            // 执行更新
            int rows = pstmt.executeUpdate();
            System.out.println("Updated " + rows + " rows");
            // 关闭资源
            pstmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}