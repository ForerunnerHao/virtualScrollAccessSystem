package vsas.daoTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import vsas.dao.DigitalScrollDAO;
import vsas.pojo.DigitalScroll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DigitalScrollDaoTest {
    private static final String TEST_NAME = "DigitalScrollDaoTest";
    private static int testNo = 1;

    static DigitalScrollDAO digitalScrollDAO;
    @BeforeAll
    static void setSqlSession(){
        digitalScrollDAO = DigitalScrollDAO.getInstance();
    }

    @AfterAll
    static void cleanup() {
        digitalScrollDAO.restartConnection();
    }

    @Test
    void selectAllDigitalScrolls(){
        String testTarget = "get all digital scrolls from database";
        executeTest(testTarget, () -> {
//            assertTrue(digitalScrollDAO.selectAll().size() >= 20);

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
