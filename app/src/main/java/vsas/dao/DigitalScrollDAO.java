package vsas.dao;

import vsas.pojo.DigitalScroll;
import vsas.pojo.Pojo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import vsas.config.MybatisConfig;

public class DigitalScrollDAO{

    private static volatile DigitalScrollDAO instance = null;

    private SqlSession session;
    private DigitalScrollDAO(){
        session = MybatisConfig.getInstance().getSqlSessionFactory().openSession();
    }

    public static DigitalScrollDAO getInstance() {
        if (instance == null) {
            // Double-Checked Locking: guarantee thread safety
            synchronized (DigitalScrollDAO.class) {
                if (instance == null) {
                    instance = new DigitalScrollDAO();
                }
            }
        }
        return instance;
    }

    public List<Pojo> selectAll() {
        return session.selectList("DigitalScrollMapper.selectAll");
    }

    public Pojo selectOne(int scroll_id) {
        return session.selectOne("DigitalScrollMapper.selectById", scroll_id);
    }

    public List<DigitalScroll> selectUploadScrolls(int uploader_id){
         return session.selectList("DigitalScrollMapper.selectUploadedScrollById", uploader_id);
    }

    public boolean delete(int scroll_id) {
        int affectedRows = session.delete("DigitalScrollMapper.deleteById", scroll_id);
        if(affectedRows > 0) {
            session.commit();
        }

        return affectedRows > 0;
    }

    public Pojo selectByName(String scroll_name){
        return session.selectOne("DigitalScrollMapper.selectByName", scroll_name);
    }

    public boolean updateWithScroll(Pojo scroll) {
        int affectedRows = session.update("DigitalScrollMapper.updateWithScroll", scroll);
        session.commit();
        return affectedRows > 0;
    }

    public boolean updateWithoutScroll(Pojo scroll) {
        int affectedRows = session.update("DigitalScrollMapper.updateWithoutScroll", scroll);
        session.commit();
        return affectedRows > 0;
    }

    public boolean create(Pojo scroll) {
        int affectedRows = session.insert("DigitalScrollMapper.insertScroll", scroll);
        session.commit();
        return affectedRows > 0;
    }

    public InputStream selectBlobStream(int scroll_id) {
        return session.selectOne("DigitalScrollMapper.selectBlobById", scroll_id);
    }

    public void restartConnection(){
        session.close();
        session = MybatisConfig.getInstance().getSqlSessionFactory().openSession();
    }

    public boolean updateBlob(String scroll_name, byte[] data) {
        Map<String, Object> params = new HashMap<>();
        params.put("scroll_name", scroll_name);
        params.put("data", data);
        int affectedRows = session.update("DigitalScrollMapper.updateBlob", params);
        session.commit();
        return affectedRows > 0;
    }

    public boolean scrollExist(String scroll_name){
        int affectRow = session.selectOne("DigitalScrollMapper.scrollExists",scroll_name);
        session.commit();
        return  affectRow > 0;
    }

    public boolean addDownloadTimes(int scroll_id){
        int affectRow = session.update("DigitalScrollMapper.incrementDownloadTimes", scroll_id);
        session.commit();
        return affectRow > 0;
    }

}
