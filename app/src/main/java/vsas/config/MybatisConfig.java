package vsas.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.InputStream;

public class MybatisConfig {

    private static volatile MybatisConfig instance = null;
    private SqlSessionFactory sqlSessionFactory;

    private MybatisConfig() {
        try{
            //1. load mybatis configuration, get factory instance
            String resource = "config/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            //1.1 update change mybatis dataSource to hikari
            Configuration configuration = sqlSessionFactory.getConfiguration();
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment newEnvironment = new Environment("development", transactionFactory, HikariCPConfig.getInstance().getDataSource());
            configuration.setEnvironment(newEnvironment);

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Can not find mybatis configuration file 'mybatis-config.xml'");
        }
    }

    public static MybatisConfig getInstance() {
        if (instance == null) {
            // Double-Checked Locking: guarantee thread safety
            synchronized (MybatisConfig.class) {
                if (instance == null) {
                    instance = new MybatisConfig();
                }
            }
        }
        return instance;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
