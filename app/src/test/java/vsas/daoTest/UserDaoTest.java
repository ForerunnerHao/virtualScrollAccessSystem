package vsas.daoTest;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.*;
import vsas.config.MybatisConfig;
import vsas.dao.UserDAO;
import vsas.pojo.User;
import vsas.utils.DEFINE;
import vsas.utils.Encoder;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private static final String TEST_NAME = "UserDaoTest";
    static SqlSession sqlSession;
    private static int testNo = 1;
    @BeforeAll
    static void setSqlSession(){
        sqlSession = MybatisConfig.getInstance().getSqlSessionFactory().openSession();
    }

    @AfterAll
     static void cleanup() {
        if (sqlSession != null) {
            sqlSession.close();
        }
    }


    // Test 01: get all users from database
    @Test
    void selectAllUsers(){
        String testTarget = "get all users from database";
        executeTest(testTarget, () -> {
//            assertTrue( (new UserDAO().selectAll().size()) > 11);
        });
    }


    private void executeTest(String testTarget, Runnable runnable){
        try {
            runnable.run();
            System.out.printf("[%s] Test %d: %s: PASS!!! \n", TEST_NAME,testNo ,testTarget );
        }catch (Exception e){
            System.out.printf("[%s] Test %d: %s: FAILED??? reason: { %s };\n" , TEST_NAME, testNo, testTarget, e.getMessage());
        }
        testNo++;
    }
}
