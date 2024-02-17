package vsas.utils;

import javafx.stage.Stage;

public class DEFINE {
    public static final long MAX_UPLOAD_SIZE = 15000000;
    public static final String USER_TYPE_ADMIN = "admin";
    public static final String USER_TYPE_REGULAR = "regular";
    public static final String USER_TYPE_GUEST = "guest";
    private static final int LOGIN_WINDOW_WIDTH = 650;
    private static final int LOGIN_WINDOW_HEIGHT = 480;
    private static final int USER_HOME_WINDOW_WIDTH = 850;
    private static final int USER_HOME_WINDOW_HEIGHT = 650;
    public static final String PAGE_NAME_LOGIN = "login";
    public static final String PAGE_NAME_USER_HOME = "userhome";
    public static final String PAGE_NAME_SIGNUP = "signup";
    public static final String PAGE_NAME_SCROLL_VIEW = "scrollview";
    public static final String PAGE_NAME_USER_VIEW = "usersview";
    public static final String PAGE_NAME_UPLOAD_SCROLL = "uploadscroll";
    public static final String PAGE_NAME_ACCOUNT_VIEW = "myaccount";
    public static final String PAGE_NAME_DOWNLOAD_HISTORY = "downloadhistory";
    public static final String PAGE_NAME_UPLOAD_HISTORY = "uploadhistory";
    public static final String PAGE_NAME_PREVIEW_SCROLL = "previewscroll";

    public static void setWindowSize(Stage currentStage, String pageName){
        switch (pageName){
            case PAGE_NAME_LOGIN,PAGE_NAME_SIGNUP -> {
                currentStage.setWidth(LOGIN_WINDOW_WIDTH);
                currentStage.setHeight(LOGIN_WINDOW_HEIGHT);
            }
            case PAGE_NAME_USER_HOME, PAGE_NAME_PREVIEW_SCROLL -> {
                currentStage.setWidth(USER_HOME_WINDOW_WIDTH);
                currentStage.setHeight(USER_HOME_WINDOW_HEIGHT);
            }
            default -> throw new RuntimeException("Do not exist the page");
        }
    }
}
