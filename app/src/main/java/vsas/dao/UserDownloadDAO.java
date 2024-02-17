package vsas.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import vsas.config.MybatisConfig;
import vsas.pojo.Pojo;
import vsas.pojo.UserDownload;

import java.util.List;

public class UserDownloadDAO {

    SqlSession session;    private static volatile UserDownloadDAO instance = null;
    private UserDownloadDAO() {
        session = MybatisConfig.getInstance().getSqlSessionFactory().openSession();
    }

    public static UserDownloadDAO getInstance() {
        if (instance == null) {
            // Double-Checked Locking: guarantee thread safety
            synchronized (DigitalScrollDAO.class) {
                if (instance == null) {
                    instance = new UserDownloadDAO();
                }
            }
        }
        return instance;
    }

    public List<Pojo> selectAll() {
        try (SqlSession session = MybatisConfig.getInstance().getSqlSessionFactory().openSession()) {
            return session.selectList("UserDownloadMapper.selectAll");
        }
    }

    public Pojo selectOne(int download_id) {
        try (SqlSession session = MybatisConfig.getInstance().getSqlSessionFactory().openSession()) {
            return session.selectOne("UserDownloadMapper.selectById", download_id);
        }
    }


    public List<UserDownload> selectDownloadDetailsByDownloaderId(int user_id) {
        try (SqlSession session = MybatisConfig.getInstance().getSqlSessionFactory().openSession()) {
            return session.selectList("UserDownloadMapper.selectDownloadDetailsByDownloaderId", user_id);
        }
    }

    public boolean delete(int download_id) {
        int affectedRows = session.delete("UserDownloadMapper.deleteById", download_id);
        session.commit();
        return affectedRows > 0;
    }

    public boolean update(Pojo user) {
        return false;
    }

    public boolean create(Pojo userDownload) {
        try (SqlSession session = MybatisConfig.getInstance().getSqlSessionFactory().openSession()) {
            int affectedRows = session.insert("UserDownloadMapper.insertScroll", userDownload);
            session.commit();
            return affectedRows > 0;
        }
    }

    public void restartConnection(){
        session.close();
        session = MybatisConfig.getInstance().getSqlSessionFactory().openSession();
    }

}
