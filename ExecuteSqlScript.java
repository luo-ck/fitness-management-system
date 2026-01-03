import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ExecuteSqlScript {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://127.0.0.1:3306/fitness?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        String username = "root";
        String password = "313813Shx@";
        String sqlScriptPath = "init_exercise_data.sql";

        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("加载MySQL驱动成功");

            // 连接数据库
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("连接数据库成功");

            // 创建Statement
            Statement stmt = conn.createStatement();

            // 读取SQL脚本文件，使用UTF-8编码
            StringBuilder sqlContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(sqlScriptPath), StandardCharsets.UTF_8))) {
                String line;
                System.out.println("开始读取SQL脚本");
                while ((line = reader.readLine()) != null) {
                    sqlContent.append(line).append("\n");
                }
            }

            // 分割SQL语句
            String[] sqlStatements = sqlContent.toString().split(";");

            // 执行每条SQL语句
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (sql.isEmpty() || sql.startsWith("--")) {
                    continue;
                }
                System.out.println("执行SQL: " + sql);
                stmt.executeUpdate(sql);
            }

            // 关闭资源
            stmt.close();
            conn.close();
            System.out.println("SQL脚本执行完成");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}