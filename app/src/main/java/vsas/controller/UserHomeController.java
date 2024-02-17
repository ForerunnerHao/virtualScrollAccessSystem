package vsas.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vsas.component.Toast;
import vsas.localstorage.LocalStorage;
import vsas.pojo.Message;
import vsas.utils.DEFINE;

public class UserHomeController implements SceneController {

    private SceneController parentsSceneController;

    @Override
    public void setParentsSceneController(SceneController parentsSceneController) {
        this.parentsSceneController = parentsSceneController;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button userHome_button_createAccount;

    @FXML
    private Button userHome_button_logout;

    @FXML
    private Button userHome_button_uploadMyScroll;

    @FXML
    private Button userHome_button_viewAllScrolls;

    @FXML
    private Button userHome_button_viewAllUsers;

    @FXML
    private Button userHome_button_viewDownloadHistory;

    @FXML
    private Button userHome_button_viewMyAccount;

    @FXML
    private Button userHome_button_viewUploadHistory;

    @FXML
    private ImageView userHome_img_logo;

    @FXML
    private ImageView userHome_img_logoName;

    @FXML
    private Label userHome_label_menu_id;

    @FXML
    private Label userHome_label_menu_type;

    @FXML
    private Label userHome_label_menu_username;

    @FXML
    private AnchorPane userHome_pane_info;

    @FXML
    private BorderPane userHome_pane_leftPane;

    @FXML
    private AnchorPane userHome_pane_logo;

    @FXML
    private AnchorPane userHome_pane_main;

    @FXML
    private VBox userHome_pane_menu;

    @FXML
    private BorderPane userHome_pane_rightPane;

    @FXML
    private Text userHome_text_menu_id;

    @FXML
    private Text userHome_text_menu_type;

    @FXML
    private Text userHome_text_menu_username;

    @FXML
    void onAdminCreateAccountAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();
            SignUpController signUpController = new SignUpController();
            signUpController.setParentsSceneController(this);
            loader.setController(signUpController);
            loader.setLocation(getClass().getResource("/layout/" + DEFINE.PAGE_NAME_SIGNUP + ".fxml"));
            AnchorPane newPane = loader.load();

            userHome_pane_rightPane.setCenter(newPane);
        }catch (IOException e){
            e.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, "Can not find the resource"), currentStage);
        }
    }

    @FXML
    void onLogoutAction(ActionEvent event) {
        // clear login user's data
        LocalStorage.getInstance().clear();

        // jump to the login page
        JumpPage.jumpToOtherPage(event, DEFINE.PAGE_NAME_LOGIN);
    }

    @FXML
    void onDownloadHistoryAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();

            // setting ScrollViewController
            DownloadHistoryController controller = new DownloadHistoryController();
            controller.setParentsSceneController(this);
            controller.setCurrentStage(currentStage);

            loader.setController(controller);
            loader.setLocation(getClass().getResource("/layout/downloadhistory.fxml"));
            AnchorPane newPane = loader.load();

            userHome_pane_rightPane.setCenter(newPane);
        }catch (IOException e){
            e.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, "Can not find the resource"), currentStage);
        }
    }

    @FXML
    void onScrollViewAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();

            // setting Controller
            ScrollViewController controller = new ScrollViewController();
            controller.setParentsSceneController(this);
            controller.setCurrentStage(currentStage);

            loader.setController(controller);
            loader.setLocation(getClass().getResource("/layout/" + DEFINE.PAGE_NAME_SCROLL_VIEW + ".fxml"));
            AnchorPane newPane = loader.load();

            userHome_pane_rightPane.setCenter(newPane);
        }catch (IOException e){
            e.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, "Cannot find the resources"), currentStage);
        }
    }

    @FXML
    void onUploadHistoryAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try{
            FXMLLoader loader = new FXMLLoader();
            //setting Upload History View controller
            UploadHistoryController controller = new UploadHistoryController();

            controller.setParentsSceneController(this);
            controller.setCurrentStage(currentStage);

            loader.setController(controller);
            loader.setLocation(getClass().getResource("/layout/"+DEFINE.PAGE_NAME_UPLOAD_HISTORY+ ".fxml"));
            AnchorPane newPane = loader.load();

            userHome_pane_rightPane.setCenter(newPane);

        }catch(IOException e){
            e.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, "Cannot find the resources"), currentStage);
        }
    }

    @FXML
    void onUploadScrollAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();
            UploadScrollController signUpController = new UploadScrollController();

            signUpController.setParentsSceneController(this);
            signUpController.setCurrentStage(currentStage);

            loader.setController(signUpController);
            loader.setLocation(getClass().getResource("/layout/" + DEFINE.PAGE_NAME_UPLOAD_SCROLL + ".fxml"));
            AnchorPane newPane = loader.load();

            userHome_pane_rightPane.setCenter(newPane);
        }catch (IOException e){
            e.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, "Can not find the resource"), currentStage);
        }
    }

    @FXML
    void onUsersViewAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();
            UsersViewController usersViewController = new UsersViewController();

            usersViewController.setParentsSceneController(this);
            usersViewController.setCurrentStage(currentStage);

            loader.setController(usersViewController);
            loader.setLocation(getClass().getResource("/layout/" + DEFINE.PAGE_NAME_USER_VIEW + ".fxml"));
            AnchorPane newPane = loader.load();

            userHome_pane_rightPane.setCenter(newPane);
        }catch (IOException e){
            e.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, "Can not find the resource"), currentStage);
        }
    }

    @FXML
    void onMyAccountAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();
            MyAccountController detailController = new MyAccountController();
            detailController.setParentsSceneController(this);
            detailController.setCurrentStage(currentStage);

            loader.setController(detailController);
            loader.setLocation(getClass().getResource("/layout/" + DEFINE.PAGE_NAME_ACCOUNT_VIEW + ".fxml"));
            AnchorPane newPane = loader.load();

            userHome_pane_rightPane.setCenter(newPane);
        }catch (IOException e){
            e.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, "Can not find the resource"), currentStage);
        }
    }

    private void hideButton(Button button) {
        button.setVisible(false);
        button.setManaged(false);
    }

    @FXML
    void initialize() {
        String user_type = LocalStorage.getInstance().getUser_type();

        // load text content, basic login user's information
        this.userHome_label_menu_id.setText(String.valueOf(LocalStorage.getInstance().getUser_id()));
        this.userHome_label_menu_username.setText(LocalStorage.getInstance().getUsername());
        this.userHome_label_menu_type.setText(user_type);

        // according to the login user's type to disable some components
        switch (user_type){
            case DEFINE.USER_TYPE_ADMIN -> {
            }
            case DEFINE.USER_TYPE_REGULAR -> {
                hideButton(this.userHome_button_viewAllUsers);
                hideButton(this.userHome_button_createAccount);
            }
            case DEFINE.USER_TYPE_GUEST -> {
                hideButton(this.userHome_button_viewAllUsers);
                hideButton(this.userHome_button_uploadMyScroll);
                hideButton(this.userHome_button_viewMyAccount);
                hideButton(this.userHome_button_viewDownloadHistory);
                hideButton(this.userHome_button_viewUploadHistory);
            }
            default -> throw new RuntimeException("Do not exist user type");
        }
    }

    private List<Button> getUserHomeButtons(){
        List<Button> buttons = new ArrayList<>();

        buttons.add(this.userHome_button_createAccount);
        buttons.add(this.userHome_button_uploadMyScroll);
        buttons.add(this.userHome_button_viewAllUsers);
        buttons.add(this.userHome_button_viewMyAccount);
        buttons.add(this.userHome_button_viewUploadHistory);
        buttons.add(this.userHome_button_viewAllScrolls);
        buttons.add(this.userHome_button_viewDownloadHistory);
        buttons.add(this.userHome_button_logout);

        return buttons;
    }

    public void disabledAllUserHomeButtons(){
        for (Button b:getUserHomeButtons()) {
            b.setDisable(true);
        }
    }

    public  void enabledAllUserHomeButtons(){
        for (Button b:getUserHomeButtons()) {
            b.setDisable(false);
        }
    }
}



