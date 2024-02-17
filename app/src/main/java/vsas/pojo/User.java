package vsas.pojo;

import java.sql.Timestamp;

public class User implements Pojo{
    private Integer user_id;
    private String username;
    private String password;
    private String user_type;
    private String fullname;
    private String phone_number;
    private String email;
    private Timestamp create_time;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "User{" +
                "\n user_id=" + user_id +
                ",\n username='" + username + '\'' +
                ",\n password='" + password + '\'' +
                ",\n user_type='" + user_type + '\'' +
                ",\n fullname='" + fullname + '\'' +
                ",\n phone_number='" + phone_number + '\'' +
                ",\n email='" + email + '\'' +
                ",\n create_time=" + create_time +
                '}';
    }
}
