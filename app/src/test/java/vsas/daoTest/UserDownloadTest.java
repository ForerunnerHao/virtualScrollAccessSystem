package vsas.daoTest;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import vsas.config.MybatisConfig;
import vsas.dao.UserDownloadDAO;
import vsas.pojo.UserDownload;


import static org.junit.jupiter.api.Assertions.*;

public class UserDownloadTest {
    private static final String TEST_NAME = "UserDownloadDaoTest";
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

    // Test 01: get all scrolls from database
    @Test
    void selectAllScrolls(){
        String testTarget = "get all scrolls from database";
        executeTest(testTarget, () -> {
//            assertTrue(new UserDownloadDAO().selectAll().size() > 10);

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
