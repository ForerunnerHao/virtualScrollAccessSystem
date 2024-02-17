package vsas.localstorage;

import vsas.pojo.DigitalScroll;
import vsas.pojo.Pojo;
import vsas.pojo.User;
import vsas.pojo.UserDownload;
import vsas.utils.DEFINE;

import java.util.ArrayList;
import java.util.List;

public class LocalStorage {
    private final String user_type;
    private final String username;
    private final int user_id;
    private static volatile LocalStorage instance = null;
    private List<DigitalScroll> scrollList;
    private List<DigitalScroll> uploadedScrollList;
    private List<UserDownload> downloadedScrollList;
    private List<User> userList;

    private boolean isTestMode = false;

    public void setTestMode(boolean isTestMode) {
        this.isTestMode = isTestMode;
    }

    public boolean isTestMode() {
        return this.isTestMode;
    }

    private LocalStorage() {
        this.user_type = DEFINE.USER_TYPE_GUEST;
        this.username = DEFINE.USER_TYPE_GUEST;
        this.user_id = 0;
        this.scrollList = new ArrayList<>();
        this.uploadedScrollList = new ArrayList<>();
        this.downloadedScrollList = new ArrayList<>();
        this.userList = new ArrayList<>();
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    private LocalStorage(int user_id, String username, String user_type) {
        this.user_type = user_type;
        this.username = username;
        this.user_id = user_id;
        this.scrollList = new ArrayList<>();
        this.uploadedScrollList = new ArrayList<>();
        this.downloadedScrollList = new ArrayList<>();
        this.userList = new ArrayList<>();

    }

    public static LocalStorage getInstance() {
        if (instance == null) {
            // Double-Checked Locking: guarantee thread safety
            synchronized (LocalStorage.class) {
                if (instance == null) {
                    instance = new LocalStorage();
                }
            }
        }
        return instance;
    }

    public static LocalStorage getInstance(int user_id, String username, String user_type) {
        if (instance == null) {
            // Double-Checked Locking: guarantee thread safety
            synchronized (LocalStorage.class) {
                if (instance == null) {
                    instance = new LocalStorage(user_id, username, user_type);
                }
            }
        }
        return instance;
    }

    //set scrolls list for upload history view
    public void setUploadedScrollList(List<DigitalScroll> uploadedScrollList){
        this.uploadedScrollList = uploadedScrollList;
    }

    //get scrolls list for upload history view
    public List<DigitalScroll> getUploadedScrollList(){return this.uploadedScrollList;}

    public List<DigitalScroll> getScrollList() {
        return scrollList;
    }

    public void setScrollList(List<DigitalScroll> scrollList) {
        this.scrollList = scrollList;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getUsername() {
        return username;
    }

    public int getUser_id() {
        return user_id;
    }

    public void clear(){
        instance = null;
    }

    public List<UserDownload> getDownloadedScrollList() {
        return downloadedScrollList;
    }

    public void setDownloadedScrollList(List<UserDownload> downloadedScrollList) {
        this.downloadedScrollList = downloadedScrollList;
    }

    @Override
    public String toString() {
        return "LocalStorage{" +
                "\nuser_type='" + user_type + '\'' +
                ", \nusername='" + username + '\'' +
                ", \nuser_id=" + user_id +
                '}';
    }
}
