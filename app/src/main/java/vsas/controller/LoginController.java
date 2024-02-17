package vsas.controller;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
import vsas.utils.DEFINE;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements SceneController {

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
    private AnchorPane LogInPane;

    @FXML
    private Button login_button_login;

    @FXML
    private Button login_button_signup;

    @FXML
    private ImageView login_img_logo;

    @FXML
    private ImageView login_img_logoName;

    @FXML
    private Label login_label_status;

    @FXML
    private ProgressIndicator login_progress_indicator;

    @FXML
    private PasswordField login_passwordField_password;

    @FXML
    private TextField login_textField_username;

    @FXML
    private Text login_text_guestAccess;

    @FXML
    private Text login_text_password;

    @FXML
    private Text login_text_username;

    @FXML
    private FontIcon login_fontIcon_success;

    @FXML
    private FontIcon login_fontIcon_fail;

    // click guest login text action
    @FXML
    void onGuestAccessAction(MouseEvent event) {
        LocalStorage.getInstance();
        System.out.println("onGuestAccessAction click");
        JumpPage.jumpToOtherPage(event, DEFINE.PAGE_NAME_USER_HOME);
    }

    // click sign up button action
    @FXML
    void onSignUpAction(ActionEvent event) {
        LocalStorage.getInstance();
        System.out.println("onSignUpAction click");
        JumpPage.jumpToOtherPage(event, DEFINE.PAGE_NAME_SIGNUP);
    }

    // click username input box action
    @FXML
    void onUsernameClickAction(MouseEvent event) {
        this.login_textField_username.getStyleClass().remove("text-field-error");
    }

    // click password input box action
    @FXML
    void onPasswordClickAction(MouseEvent event) {
        this.login_passwordField_password.getStyleClass().remove("text-field-error");
    }

    // login button action
    @FXML
    void onLoginAction(ActionEvent event) {
        //Disable some specify components to prevent user interactions
        disabledComponents();

        // Set the status label and progress indicator to be visible:
        this.login_fontIcon_success.setVisible(false);
        this.login_fontIcon_fail.setVisible(false);
        this.login_label_status.setText("Please wait system connecting... ");
        this.login_label_status.setVisible(true);
        this.login_progress_indicator.setVisible(true);

        //Create a background task (loginTask)
        Task<Message> loginTask = new Task<Message>() {
            @Override
            protected Message call() {

                userDAO.restartConnection();

                String input_username = login_textField_username.getText().trim();
                String input_password = login_passwordField_password.getText().trim();

                // verify the user input username and password
                Message feedback = verifyInput(input_username, input_password);
                if (feedback.getStatus() == Message.Status.FAIL) return feedback;

                // user login
                if (userDAO.userLogin(input_username, input_password)){
                    return new Message(Message.Status.SUCCESS, "Login successfully!!!");
                }else {
                    return new Message(Message.Status.FAIL, "Username or password is invalid!!!");
                }
            }
        };

        // Setting the statement that is executed when the login task runs successfully
        loginTask.setOnSucceeded(e -> {
            // if login succeed
            if (loginTask.getValue().getStatus() == Message.Status.SUCCESS) {
                // pop up toast
                Toast.show(loginTask.getValue(), (Stage) ((Node) event.getSource()).getScene().getWindow());
                System.out.println(loginTask.getValue().getMsg());

                // set UI components' status
                login_progress_indicator.setVisible(false);
                login_fontIcon_success.setVisible(true);
                login_label_status.setText(loginTask.getValue().getMsg());
                login_textField_username.getStyleClass().add("text-field-success");
                login_passwordField_password.getStyleClass().add("text-field-success");

                // wait 1 second then jump to the userHome page
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(ev -> {

                    // jump to the user home page
                    JumpPage.jumpToOtherPage(event, DEFINE.PAGE_NAME_USER_HOME);

                    // Re-enable the root pane after the pause
                    enabledComponents();
                });
                pause.play();
            }
            // if login failed
            else if (loginTask.getValue().getStatus() == Message.Status.FAIL){
                // pop up toast
                Toast.show(loginTask.getValue(), (Stage) ((Node) event.getSource()).getScene().getWindow());
                System.out.println(loginTask.getValue().getMsg());

                // set UI components' status
                login_progress_indicator.setVisible(false);
                login_fontIcon_fail.setVisible(true);
                login_label_status.setText(loginTask.getValue().getMsg());
                login_textField_username.getStyleClass().add("text-field-error");
                login_passwordField_password.getStyleClass().add("text-field-error");

                // Re-enable the root pane after the pause
                enabledComponents();

            }else {
                // Re-enable the root pane after the pause
                enabledComponents();
                throw new RuntimeException("Invalid status!!!");
            }
        });

        // Setting the statement that is executed when the login task runs failed
        loginTask.setOnFailed(e -> {
            // pop up toast
            Toast.show(new Message(Message.Status.FAIL, "Error: Unable to connect to server!!!"), (Stage) ((Node) event.getSource()).getScene().getWindow());
            System.out.println("Error: Unable to connect to server!!!");

            // set UI components' status
            login_progress_indicator.setVisible(false);
            login_fontIcon_fail.setVisible(true);
            login_label_status.setText("Error: Unable to connect to server!!!");

            // Re-enable the root pane after the pause
            enabledComponents();
        });

        // create a new thread to execute the loginTask, so it can not affect the UI component render
        new Thread(loginTask).start();

    }

    public LoginController() {

    }

    @FXML
    void initialize() {
        // init specify UI component status
        this.login_progress_indicator.setVisible(false);
        this.login_label_status.setVisible(false);
        this.login_fontIcon_success.setVisible(false);
        this.login_fontIcon_fail.setVisible(false);
    }

    private Message verifyInput(String username, String password){
        // username or password is empty
        if (username.isEmpty() || password.isEmpty()){
            return new Message(Message.Status.FAIL, "username or password is empty");
        }

        // username or password is null

        // username or password has ' ' char
        if (username.matches(".*\\s.*") || password.matches(".*\\s.*")){
            return new Message(Message.Status.FAIL, "username or password has space char");
        }

        // username or password length less than 8
        if (username.length() <8 || password.length() < 8){
            return new Message(Message.Status.FAIL, "username or password length less than 8");
        }

        return new Message();
    }

    private void disabledComponents(){
        this.login_button_login.setDisable(true);
        this.login_button_signup.setDisable(true);
        this.login_passwordField_password.setDisable(true);
        this.login_textField_username.setDisable(true);
        this.login_text_guestAccess.setDisable(true);
    }

    private void enabledComponents(){
        this.login_button_login.setDisable(false);
        this.login_button_signup.setDisable(false);
        this.login_passwordField_password.setDisable(false);
        this.login_textField_username.setDisable(false);
        this.login_text_guestAccess.setDisable(false);
    }

    public PasswordField getLogin_passwordField_password() {
        return login_passwordField_password;
    }

    public TextField getLogin_textField_username() {
        return login_textField_username;
    }
}
