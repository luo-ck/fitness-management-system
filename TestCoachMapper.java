import com.fitness.system.entity.Coach;
import com.fitness.system.mapper.CoachMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class TestCoachMapper {
    public static void main(String[] args) {
        try {
            // 加载MyBatis配置文件
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            // 创建SqlSessionFactory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            // 打开SqlSession
            SqlSession sqlSession = sqlSessionFactory.openSession();
            // 获取Mapper
            CoachMapper coachMapper = sqlSession.getMapper(CoachMapper.class);
            
            // 测试查询教练
            Coach coach = coachMapper.selectCoachByUsername("老牛");
            System.out.println("Coach: " + coach);
            
            // 关闭SqlSession
            sqlSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}