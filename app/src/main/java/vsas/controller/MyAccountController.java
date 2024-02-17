package vsas.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vsas.component.Toast;
import vsas.dao.UserDAO;
import vsas.localstorage.LocalStorage;
import vsas.pojo.Message;
import vsas.pojo.User;
import vsas.utils.Encoder;

import java.util.Optional;

public class MyAccountController implements SceneController{

    private Stage currentStage;

    private SceneController parentsSceneController;

    private final UserDAO userDAO = UserDAO.getInstance();

    private User currentLoginUser;

    @FXML
    private AnchorPane UserDetailPane;

    @FXML
    private AnchorPane userdetail_anchorPane_changePasswordRoot;

    @FXML
    private AnchorPane userdetail_anchorPane_layoutRoot;

    @FXML
    private Button userdetail_button_cancelChangePassword;

    @FXML
    private Button userdetail_button_changePassword;

    @FXML
    private Button userdetail_button_comfirmChangePassword;

    @FXML
    private Button userdetail_button_save;

    @FXML
    private PasswordField userdetail_passwordField_newPassword;

    @FXML
    private PasswordField userdetail_passwordField_repeatPassword;

    @FXML
    private TextField userdetail_textField_email;

    @FXML
    private TextField userdetail_textField_fullname;

    @FXML
    private TextField userdetail_textField_phonenumber;

    @FXML
    private TextField userdetail_textField_username;

    @FXML
    private TextField userdetail_textField_usertype;

    @FXML
    private Text userdetail_text_email;

    @FXML
    private Text userdetail_text_fullname;

    @FXML
    private Text userdetail_text_myaccountinfo;

    @FXML
    private Text userdetail_text_password;

    @FXML
    private Text userdetail_text_phonenumber;

    @FXML
    private Text userdetail_text_username;

    @FXML
    private Text userdetail_text_usertype;

    @FXML
    void onChangePassword(ActionEvent event) {
        this.userdetail_anchorPane_layoutRoot.setVisible(false);
        this.userdetail_anchorPane_changePasswordRoot.setVisible(true);
    }

    @FXML
    void onChangePasswordCancel(ActionEvent event) {
        this.userdetail_anchorPane_layoutRoot.setVisible(false);
        this.userdetail_anchorPane_changePasswordRoot.setVisible(false);
        this.userdetail_passwordField_newPassword.setText(null);
        this.userdetail_passwordField_repeatPassword.setText(null);
    }

    @FXML
    void onChangePasswordConfirm(ActionEvent event) {
        // disabled
        disableLoadingUserData();

        Task<Message> changePassword = changePasswordTask();

        changePassword.setOnSucceeded(e ->{
            Toast.show(changePassword.getValue(), currentStage);
            if (changePassword.getValue().getStatus() == Message.Status.SUCCESS){
                this.userdetail_passwordField_newPassword.setText("");
                this.userdetail_passwordField_repeatPassword.setText("");
                enableLoadingUserData();
            }else {
                enableLoadingUserData();
                this.userdetail_anchorPane_changePasswordRoot.setVisible(true);
            }
        });

        changePassword.setOnFailed(e->{
            Exception exception = (Exception) e.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), currentStage);

            this.userdetail_passwordField_newPassword.setText("");
            this.userdetail_passwordField_repeatPassword.setText("");

            enableLoadingUserData();
        });

        new Thread(changePassword).start();
    }

    @FXML
    void onSaveUserInfoAction(ActionEvent event) {

        // alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change user info");
        alert.setHeaderText("Are you sure, you want to change");

        // Create custom ButtonTypes
        ButtonType okButton = new ButtonType("Continue");
        ButtonType cancelButton = new ButtonType("Cancel");

        // Set the alert's button types
        alert.getButtonTypes().setAll(okButton, cancelButton);

        // Capture the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // If user chooses OK, continue; otherwise, return and don't download
        if (result.isPresent() && result.get() == okButton) {

            // disabled
            disableLoadingUserData();

            String newEmail = userdetail_textField_email.getText();
            String newPhoneNumber = userdetail_textField_phonenumber.getText();
            String newFullName = userdetail_textField_fullname.getText();

            if (newEmail.isEmpty() || newPhoneNumber.isEmpty() || newFullName.isEmpty()){
                Toast.show(new Message(Message.Status.WARNING, "empty input"),currentStage);
                enableLoadingUserData();
                return;
            }

            // Check email format
            String emailPattern = "^[a-zA-Z\\d._%+-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,6}$";
            if (!newEmail.matches(emailPattern)){
                Toast.show(new Message(Message.Status.FAIL, "invalid mail format"),currentStage);
                enableLoadingUserData();
                return;
            }

            Task<Message> changeUserInfo = changeUserInfo(newEmail, newFullName, newPhoneNumber);

            changeUserInfo.setOnSucceeded(e ->{
                Toast.show(changeUserInfo.getValue(), currentStage);
                refreshData();
            });

            changeUserInfo.setOnFailed(e->{
                Exception exception = (Exception) e.getSource().getException();
                exception.printStackTrace();
                Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), currentStage);
            });

            new Thread(changeUserInfo).start();
        }

        enableLoadingUserData();
    }

    @FXML
    void initialize() {
        // disabled layout
        this.userdetail_anchorPane_layoutRoot.setVisible(false);
        refreshData();
    }

    private Task<Message> getDataTask(){
        return new Task<Message>() {
            @Override
            protected Message call() throws Exception {

                userDAO.restartConnection();
                int userId = LocalStorage.getInstance().getUser_id();
                currentLoginUser = (User)userDAO.selectOne(userId);

                if (currentLoginUser == null) return new Message(Message.Status.FAIL, "User info can not find");

                return new Message(Message.Status.SUCCESS, "load data successfully");
            }
        };
    }

    private Task<Message> changePasswordTask(){
        return new Task<>() {
            @Override
            protected Message call() {
                userDAO.restartConnection();

                if (currentLoginUser == null) return new Message(Message.Status.FAIL, "user data failed");

                String newPassword = userdetail_passwordField_newPassword.getText();
                String repeatPassword = userdetail_passwordField_repeatPassword.getText();

                if (newPassword == null || newPassword.isEmpty()|| repeatPassword == null ||  repeatPassword.isEmpty() )
                    return new Message(Message.Status.WARNING, "empty input");

                if (newPassword.length() < 8 || repeatPassword.length() < 8)
                    return new Message(Message.Status.WARNING, "password length do not match");

                if (!newPassword.equals(repeatPassword))
                    return new Message(Message.Status.WARNING, "input password do not match");

                currentLoginUser.setPassword(Encoder.encode(newPassword));

                if (!userDAO.update(currentLoginUser))
                    new Message(Message.Status.FAIL, "change password failed please try again later");
                return new Message(Message.Status.SUCCESS, "change password successfully");
            }
        };
    }

    private Task<Message> changeUserInfo(String newEmail, String newFullName, String newPhoneNumber){
        return new Task<>() {
            @Override
            protected Message call() {

                userDAO.restartConnection();
                if (currentLoginUser == null) return new Message(Message.Status.FAIL, "user data failed");

                currentLoginUser.setEmail(newEmail);
                currentLoginUser.setFullname(newFullName);
                currentLoginUser.setPhone_number(newPhoneNumber);

                if (!userDAO.update(currentLoginUser))
                    new Message(Message.Status.FAIL, "change user info failed please try again later");
                return new Message(Message.Status.SUCCESS, "change user info successfully");
            }
        };
    }

    private void refreshData(){
        Task<Message> getData = getDataTask();

        // disabled components
        disableLoadingUserData();

        getData.setOnSucceeded(e ->{
            Toast.show(getData.getValue(), currentStage);
            if (getData.getValue().getStatus() == Message.Status.SUCCESS) populateUserDetails(currentLoginUser);
            enableLoadingUserData();
        });

        getData.setOnFailed(e->{
            Exception exception = (Exception) e.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), currentStage);
            enableLoadingUserData();
        });

        new Thread(getData).start();
    }


    private void populateUserDetails(User user) {
        if (user != null) {
            userdetail_textField_username.setText(user.getUsername());
            userdetail_textField_email.setText(user.getEmail());
            userdetail_textField_fullname.setText(user.getFullname());
            userdetail_textField_phonenumber.setText(user.getPhone_number());
            userdetail_textField_usertype.setText(user.getUser_type());

            // Making username and user type non-editable
            userdetail_textField_username.setDisable(true);
            userdetail_textField_usertype.setDisable(true);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void setParentsSceneController(SceneController parentsSceneController) {
        this.parentsSceneController = parentsSceneController;
    }

    public void  setCurrentStage(Stage currentStage){
        this.currentStage = currentStage;
    }

    private void enableLoadingUserData(){
        // open the progressBar pane
        this.userdetail_anchorPane_layoutRoot.setVisible(false);
        this.userdetail_anchorPane_changePasswordRoot.setVisible(false);

        this.userdetail_textField_email.setDisable(false);
        this.userdetail_textField_fullname.setDisable(false);
        this.userdetail_textField_phonenumber.setDisable(false);

        this.userdetail_button_save.setDisable(false);
        this.userdetail_button_changePassword.setDisable(false);

        UserHomeController userHomeController = (UserHomeController) this.parentsSceneController;
        userHomeController.enabledAllUserHomeButtons();
    }

    private void disableLoadingUserData(){
        // close the progressBar pane
        this.userdetail_anchorPane_layoutRoot.setVisible(true);
        this.userdetail_anchorPane_changePasswordRoot.setVisible(false);

        this.userdetail_textField_email.setDisable(true);
        this.userdetail_textField_fullname.setDisable(true);
        this.userdetail_textField_phonenumber.setDisable(true);

        this.userdetail_button_save.setDisable(true);
        this.userdetail_button_changePassword.setDisable(true);

        UserHomeController userHomeController = (UserHomeController) this.parentsSceneController;
        userHomeController.disabledAllUserHomeButtons();
    }

}
