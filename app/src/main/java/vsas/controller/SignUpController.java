package vsas.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import vsas.component.Toast;
import vsas.dao.UserDAO;
import vsas.localstorage.LocalStorage;
import vsas.pojo.Message;
import vsas.pojo.User;
import vsas.utils.DEFINE;
import vsas.utils.Encoder;

public class SignUpController implements SceneController {

    private SceneController parentsSceneController;

    private final UserDAO userDAO = UserDAO.getInstance();

    @Override
    public void setParentsSceneController(SceneController parentsSceneController) {
        this.parentsSceneController = parentsSceneController;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane Register_Pane;

    @FXML
    private Button signup_button_backLogin;

    @FXML
    private Button signup_button_createAccount;

    @FXML
    private ComboBox<?> signup_comboBox_userType;

    @FXML
    private FontIcon signup_fontIcon_failed;

    @FXML
    private FontIcon signup_fontIcon_succeed;

    @FXML
    private Label signup_label_email;

    @FXML
    private Label signup_label_fullname;

    @FXML
    private Label signup_label_password;

    @FXML
    private Label signup_label_phoneNumber;

    @FXML
    private Label signup_label_status;

    @FXML
    private Label signup_label_userType;

    @FXML
    private Label signup_label_username;

    @FXML
    private PasswordField signup_passwordField_password;

    @FXML
    private ProgressIndicator signup_progress_indicator;

    @FXML
    private TextField signup_textField_email;

    @FXML
    private TextField signup_textField_fullName;

    @FXML
    private TextField signup_textField_phoneNumber;

    @FXML
    private TextField signup_textField_username;

    @FXML
    private Text signup_text_guestAccess;

    @FXML
    private Text signup_text_title;

    @FXML
    void onBackLoginAction(ActionEvent event) {
        LocalStorage.getInstance().clear();
        JumpPage.jumpToOtherPage(event, DEFINE.PAGE_NAME_LOGIN);
    }
    private static int creatTimes = 0;

    @FXML
    void onCreateAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // disabled the interactive component
        disabledComponents();

        // Set the status label and progress indicator to be visible:
        this.signup_fontIcon_succeed.setVisible(false);
        this.signup_fontIcon_failed.setVisible(false);
        this.signup_label_status.setText("Please wait system connecting... ");
        this.signup_label_status.setVisible(true);
        this.signup_progress_indicator.setVisible(true);

        //create a create task
        Task<Message> createTask = new Task<Message>() {
            @Override
            protected Message call() throws Exception {

                // check user input
                Message message = passInputCheck();

                // if input check do not pass
                if (message.getStatus() == Message.Status.FAIL) return message;

                // check if the input username exists
                message = usernameIsExist();

                // if username exist
                if (message.getStatus() == Message.Status.FAIL) return message;

                // create a new account
                User user = new User();
                user.setPhone_number(signup_textField_phoneNumber.getText());
                user.setUser_type(signup_comboBox_userType.getValue().toString());
                user.setPassword(Encoder.encode(signup_passwordField_password.getText()));
                user.setFullname(signup_textField_fullName.getText());
                user.setEmail(signup_textField_email.getText());
                user.setUsername(signup_textField_username.getText());

                // submit the statement
                userDAO.restartConnection();
                if (userDAO.create(user)){
                    return new Message(Message.Status.SUCCESS, "create successfully");
                }else {

                    // create failed
                    if (creatTimes <= 3){
                        creatTimes ++;
                        return new Message(Message.Status.FAIL, "Create failed, Please try again later");
                    }else {
                        // create times over 3
                        return new Message(Message.Status.FAIL, "Server error, Please try contact with admin");
                    }
                }
            }
        };

        // Setting the statement that is executed when the creating task runs successfully
        createTask.setOnSucceeded(e -> {
            Toast.show(createTask.getValue(), currentStage);
            if (createTask.getValue().getStatus() == Message.Status.SUCCESS){

                // set component status
                this.signup_progress_indicator.setVisible(false);
                this.signup_fontIcon_succeed.setVisible(true);
                this.signup_label_status.setText(createTask.getValue().getMsg());

                // wait 2 second then jump to the login page if user is guest
                if (LocalStorage.getInstance().getUser_type().equals(DEFINE.USER_TYPE_GUEST)){

                    this.signup_label_status.setText("Please wait 2 second the page will redirect to login page");

                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(ev -> {

                        // clean user data
                        LocalStorage.getInstance().clear();

                        // jump to the user home page
                        JumpPage.jumpToOtherPage(event, DEFINE.PAGE_NAME_LOGIN);

                        // Re-enable the root pane after the pause
                        enabledComponents();
                    });
                    pause.play();
                }else{

                    enabledComponents();
                }

            } else if (createTask.getValue().getStatus() == Message.Status.FAIL){
                System.out.println(createTask.getValue().getMsg());

                // set UI components' status
                signup_progress_indicator.setVisible(false);
                signup_fontIcon_failed.setVisible(true);
                signup_label_status.setText(createTask.getValue().getMsg());

                // enable the component
                enabledComponents();

            }else {
                // Invalid status!!!
                enabledComponents();
                throw new RuntimeException("Invalid status!!!");
            }
        });

        // Setting the statement that is executed when the login task runs failed
        createTask.setOnFailed(e -> {
            Toast.show(createTask.getValue(), currentStage);

            // enabled components
            enabledComponents();
        });

        // create a new thread to execute the createTask, so it can not affect the UI component render
        new Thread(createTask).start();

    }

    // click guest account link
    @FXML
    void onGuestAccessAction(MouseEvent event) {
        LocalStorage.getInstance().clear();
        JumpPage.jumpToOtherPage(event, DEFINE.PAGE_NAME_USER_HOME);
    }

    @FXML
    void initialize() {
        // set visible to false
        this.signup_fontIcon_failed.setVisible(false);
        this.signup_fontIcon_succeed.setVisible(false);
        this.signup_progress_indicator.setVisible(false);
        this.signup_label_status.setVisible(false);
        if (this.parentsSceneController != null) this.signup_text_guestAccess.setVisible(false);

        //set choice box statement
        final String user_type = LocalStorage.getInstance().getUser_type();
        switch (user_type) {
            case DEFINE.USER_TYPE_ADMIN -> {
                // hidde login button
                this.signup_button_backLogin.setVisible(false);
                this.signup_button_backLogin.setDisable(true);

                // hidde guest login link
                this.signup_text_guestAccess.setVisible(false);
                this.signup_text_guestAccess.setDisable(true);
            }
            case DEFINE.USER_TYPE_GUEST, DEFINE.USER_TYPE_REGULAR -> {

                // lock the user type option
                this.signup_comboBox_userType.setDisable(true);
            }
        }
    }

    private Message passInputCheck() {
        // Check if any input field is empty
        if (    this.signup_textField_username.getText().isEmpty() ||
                this.signup_textField_email.getText().isEmpty() ||
                this.signup_textField_fullName.getText().isEmpty() ||
                this.signup_textField_phoneNumber.getText().isEmpty() ||
                this.signup_passwordField_password.getText().isEmpty()) {
            return new Message(Message.Status.FAIL, "input box can not be empty");
        }

        // Check if username and password is at least 8 characters and doesn't contain spaces
        if (this.signup_textField_username.getText().length() < 8 ||
                this.signup_textField_username.getText().contains(" ") ||
                this.signup_passwordField_password.getText().length() < 8 ||
                this.signup_passwordField_password.getText().contains(" ")
        ) {
            return new Message(Message.Status.FAIL, "invalid username or password");
        }

        // Check email format
        String emailPattern = "^[a-zA-Z\\d._%+-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,6}$";
        if (!this.signup_textField_email.getText().matches(emailPattern)) {
            return new Message(Message.Status.FAIL, "invalid mail format");
        }

        return new Message();
    }

    private Message usernameIsExist(){
        String inputUsername = this.signup_textField_username.getText();
        userDAO.restartConnection();
        if(userDAO.usernameIsExist(inputUsername))
            return new Message(Message.Status.FAIL, "username is exist");
        else
            return new Message();
    }

    private void disabledComponents(){

        // disabled all input text box
        this.signup_textField_username.setDisable(true);
        this.signup_textField_email.setDisable(true);
        this.signup_textField_fullName.setDisable(true);
        this.signup_textField_phoneNumber.setDisable(true);
        this.signup_passwordField_password.setDisable(true);
        this.signup_comboBox_userType.setDisable(true);

        // disable other components
        this.signup_button_backLogin.setDisable(true);
        this.signup_button_createAccount.setDisable(true);
        this.signup_text_guestAccess.setDisable(true);

        // set parent scene button components
        if (this.parentsSceneController != null){
            UserHomeController controller = (UserHomeController)this.parentsSceneController;
            controller.disabledAllUserHomeButtons();
        }
    }

    private void enabledComponents(){
        // disabled all input text box
        this.signup_textField_username.setDisable(false);
        this.signup_textField_email.setDisable(false);
        this.signup_textField_fullName.setDisable(false);
        this.signup_textField_phoneNumber.setDisable(false);
        this.signup_passwordField_password.setDisable(false);
        this.signup_comboBox_userType.setDisable(false);

        // disable other components
        this.signup_button_backLogin.setDisable(false);
        this.signup_button_createAccount.setDisable(false);
        this.signup_text_guestAccess.setDisable(false);

        // set parent scene button components
        if (this.parentsSceneController != null){
            UserHomeController controller = (UserHomeController)this.parentsSceneController;
            controller.enabledAllUserHomeButtons();
        }

    }
}
