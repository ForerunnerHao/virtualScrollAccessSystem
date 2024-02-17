package vsas.dao;

import org.apache.ibatis.session.SqlSession;
import vsas.config.MybatisConfig;
import vsas.localstorage.LocalStorage;
import vsas.pojo.Pojo;
import vsas.pojo.User;
import vsas.utils.Encoder;

import java.util.List;

public class UserDAO{
    SqlSession session;

    private static volatile UserDAO instance = null;

    public static UserDAO getInstance() {
        if (instance == null) {
            // Double-Checked Locking: guarantee thread safety
            synchronized (DigitalScrollDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
    }
    
    private UserDAO() {
        session = MybatisConfig.getInstance().getSqlSessionFactory().openSession();
    }

    public List<User> selectAll(int admin_user_id) {
        return session.selectList("UserMapper.selectAll", admin_user_id);
    }

    public Pojo selectOne(int user_id) {
        return session.selectOne("UserMapper.selectById", user_id);
    }

    public boolean delete(int user_id) {
        int affectedRows = session.delete("UserMapper.deleteById", user_id);
        session.commit();
        return affectedRows > 0;
    }

    public boolean update(Pojo user) {
        int affectedRows = session.update("UserMapper.updateById", user);
        session.commit();
        return affectedRows > 0;
    }

    public  boolean updateUserInfo(Pojo user){
        int affectedRows = session.update("UserMapper.updateUserInfo", user);
        session.commit();
        return affectedRows > 0;
    }

    public boolean create(Pojo user) {
        int affectedRows = session.insert("UserMapper.insertUser", user);
        session.commit();
        return affectedRows > 0;
    }

    public boolean userLogin(String username, String password){

        User loginUser = session.selectOne("UserMapper.userLogin",username);

        // can not find the username return false
        if (loginUser == null) return false;

        session.close();
        
        // return the verified result
        if (Encoder.verify(loginUser.getPassword(), password)){
            // pass the verification, set the local
            LocalStorage.getInstance(loginUser.getUser_id(), loginUser.getUsername(), loginUser.getUser_type());
            return true;
        }else {
            return false;
        }

    }

    public boolean isAdmin(int user_id){
        int affectRows = session.selectOne("UserMapper.isAdmin", user_id);
        session.commit();
        return affectRows > 0;
    }

    public boolean usernameIsExist(String username){
        int affectRows = session.selectOne("UserMapper.userExists", username);
        session.commit();
        return affectRows > 0;
    }

    public Pojo selectByName(String username){
        return session.selectOne("UserMapper.selectByUsername", username);
    }

    public void restartConnection(){
        session.close();
        session = MybatisConfig.getInstance().getSqlSessionFactory().openSession();
    }
}
