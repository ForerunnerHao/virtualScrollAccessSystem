package vsas.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.util.Properties;

public class HikariCPConfig {
    private static volatile HikariCPConfig instance = null;
    private  HikariDataSource dataSource;

    private HikariCPConfig(){
        try {
            //0. Load properties from Hikari's configuration file
            Properties properties = new Properties();
            properties.load(Resources.getResourceAsStream("config/hikari.properties"));
            HikariConfig config = new HikariConfig(properties);
            dataSource = new HikariDataSource(config);
        }catch (IOException e){
            System.out.println("Can not find HikariCP configuration file 'hikari.properties'");
            e.printStackTrace();
        }
    }

    public static HikariCPConfig getInstance() {
        if (instance == null) {
            // Double-Checked Locking: guarantee thread safety
            synchronized (HikariCPConfig.class) {
                if (instance == null) {
                    instance = new HikariCPConfig();
                }
            }
        }
        return instance;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
