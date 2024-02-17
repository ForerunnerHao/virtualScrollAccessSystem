package vsas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import vsas.component.Toast;
import vsas.dao.UserDAO;
import vsas.localstorage.LocalStorage;
import vsas.pojo.DigitalScroll;
import vsas.pojo.Message;
import vsas.pojo.User;
import vsas.pojo.UserDownload;

public class UsersViewController implements SceneController{
    private Stage currentStage;
    private UserHomeController parentsSceneController;
    private final UserDAO userDAO = UserDAO.getInstance();

    private static User selectUser = null;

    @Override
    public void setParentsSceneController(SceneController parentsSceneController) {
        this.parentsSceneController = (UserHomeController) parentsSceneController;
    }

    public void  setCurrentStage(Stage currentStage){
        this.currentStage = currentStage;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane usersview_anchorPane_loadingPaneRoot;

    @FXML
    private AnchorPane usersview_anchorPane_userDetailPaneRoot;

    @FXML
    private Button usersview_button_back;

    @FXML
    private Button usersview_button_delete;

    @FXML
    private Button usersview_button_refresh;

    @FXML
    private Button usersview_button_save;

    @FXML
    private Button usersview_button_search;

    @FXML
    private ComboBox<String> usersview_comboBox_userType;

    @FXML
    private ComboBox<String> usersview_combobox_search;

    @FXML
    private Label usersview_label_searchResult;

    @FXML
    private TableColumn<User, String> usersview_tableColum_userEmail;

    @FXML
    private TableColumn<User, Integer> usersview_tableColum_userID;

    @FXML
    private TableColumn<User, String> usersview_tableColum_userName;

    @FXML
    private TableColumn<User, String> usersview_tableColum_userPhoneNumber;

    @FXML
    private TableColumn<User, String> usersview_tableColum_userType;

    @FXML
    private TableView<User> usersview_tableview_scrollList;

    @FXML
    private TextField usersview_textField_userEmail;

    @FXML
    private TextField usersview_textField_userFullname;

    @FXML
    private TextField usersview_textField_userId;

    @FXML
    private TextField usersview_textField_userPhoneNumber;

    @FXML
    private TextField usersview_textField_username;

    @FXML
    private TextField usersview_textfield_searchBox;

    @FXML
    void onBackAction(ActionEvent event) {
        clearTextContent();
        this.usersview_anchorPane_userDetailPaneRoot.setVisible(false);
    }

    @FXML
    void onClickOneRowAction(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();

        // find TableRow
        while (clickedNode != null && !(clickedNode instanceof TableRow)) {
            clickedNode = clickedNode.getParent();
        }

        // if node not equal null, then get its data
        if (clickedNode != null) {
            TableRow<?> row = (TableRow<?>) clickedNode;

            if (row.getItem() != null) {
                User selectedUser = (User) row.getItem();

                int selectedId = selectedUser.getUser_id();
                System.out.println("Selected user ID: " + selectedId);
                selectUser = selectedUser;
//                System.out.println(selectUser);
                // Open the sub-pane here
                openSubScene(selectedUser);
            }
        }
    }

    @FXML
    void onDeleteAction(ActionEvent event) {
        deleteUser();
    }

    @FXML
    void onRefreshAction(ActionEvent event) {
        clearTextContent();
        getAllUsers();
    }

    @FXML
    void onSaveAction(ActionEvent event) {
        changeUserInfo();
    }

    @FXML
    void onScrollSearchAction(ActionEvent event) {
        disabledComponents();
        String searchContent = this.usersview_textfield_searchBox.getText().trim().toLowerCase();
        List<User> searchUsers = new ArrayList<>();
        List<User> localUsers = LocalStorage.getInstance().getUserList();
        String selectedItem = usersview_combobox_search.getSelectionModel().getSelectedItem();

        if (searchContent.isEmpty()){
            searchUsers = localUsers;
        } else {
            switch (selectedItem) {
                case "UserName":
                    for (User ds: localUsers) {
                        if (ds.getUsername().toLowerCase().contains(searchContent)) {
                            searchUsers.add(ds);
                        }
                    }
                    break;
                case "UserID":
                    for (User ds: localUsers) {
                        if (String.valueOf(ds.getUser_id()).contains(searchContent)) {
                            searchUsers.add(ds);
                        }
                    }
                    break;
                case "All":
                default:
                    for (User ds: localUsers) {
                        if (ds.getUsername().toLowerCase().contains(searchContent)
                                || String.valueOf(ds.getUser_id()).contains(searchContent)
                            // Add more conditions here for other fields
                        ) {
                            searchUsers.add(ds);
                        }
                    }
                    break;
            }
        }

        this.usersview_label_searchResult.setText("Total "+ searchUsers.size() +" results");
        setTableItems(searchUsers);
        enabledComponents();
    }

    @FXML
    void initialize() {
        // disabled components
        this.usersview_anchorPane_loadingPaneRoot.setVisible(false);
        this.usersview_anchorPane_userDetailPaneRoot.setVisible(false);

        // setting tableView placeholder
        Label placeholderLabel = new Label("The data in the table is empty");
        this.usersview_tableview_scrollList.setPlaceholder(placeholderLabel);

        // get local userList
        List<User> userList = LocalStorage.getInstance().getUserList();

        if (userList.isEmpty()) {

            // loading data disabled components
            disabledComponents();

            // new thread to get data
            getAllUsers();
        } else {
            Toast.show(new Message(Message.Status.SUCCESS, "Local cache load succeed"), this.currentStage);

            // load cache data
            setTableItems(userList);

            // set the result label
            this.usersview_label_searchResult.setText("Total " + LocalStorage.getInstance().getUserList().size() + " results");
        }
    }

    private void getAllUsers() {
        disabledComponents();

        // create a task
        Task<Message> getUsers = new Task<>() {
            @Override
            protected Message call() {

                // get data from database
                userDAO.restartConnection();
                List<User> userList = userDAO.selectAll(LocalStorage.getInstance().getUser_id());

                if (userList== null || userList.isEmpty()){
                    return new Message(Message.Status.FAIL, "Data get failed, please try again");
                }
                LocalStorage.getInstance().setUserList(userList);

                return new Message(Message.Status.SUCCESS, "Data get succeed");
            }
        };

        getUsers.setOnSucceeded(e -> {

            Message returnMsg = getUsers.getValue();
            Toast.show(returnMsg, this.currentStage);
            enabledComponents();

            // set table items
            setTableItems(LocalStorage.getInstance().getUserList());

            // set result text
            usersview_label_searchResult.setText("Total " + LocalStorage.getInstance().getUserList().size() + " results");

            if (returnMsg.getStatus() != Message.Status.SUCCESS && returnMsg.getStatus() != Message.Status.FAIL) {
                // Invalid status!!!
                throw new RuntimeException("Invalid status!!!");
            }
        });

        getUsers.setOnFailed(e -> {
            Exception exception = (Exception) e.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), this.currentStage);
            enabledComponents();
        });

        new Thread(getUsers).start();
    }

    private void changeUserInfo(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change user's info");
        alert.setHeaderText("Do your want to change user '" + this.usersview_textField_username.getText() + "' info ?");

        // Create custom ButtonTypes
        ButtonType okButton = new ButtonType("Yes");
        ButtonType cancelButton = new ButtonType("No");

        // Set the alert's button types
        alert.getButtonTypes().setAll(okButton, cancelButton);

        // Capture the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // If user chooses OK, continue; otherwise, return and don't download
        if (result.isPresent() && result.get() == okButton) {
            disabledComponents();
            changeTask();
        }
    }

    private void setTableItems(List<User> userList) {
        // set table columns
        this.usersview_tableColum_userID.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        usersview_tableColum_userName.setCellValueFactory(new PropertyValueFactory<>("username"));
        usersview_tableColum_userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        usersview_tableColum_userPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        usersview_tableColum_userType.setCellValueFactory(new PropertyValueFactory<>("user_type"));

        ObservableList<User> data = FXCollections.observableArrayList(userList);
        usersview_tableview_scrollList.setItems(data);

    }

    private void openSubScene(User user){
        this.usersview_anchorPane_userDetailPaneRoot.setVisible(true);
        setTextContent(user);
    }

    private void setTextContent(User user){
        this.usersview_textField_userId.setText(String.valueOf(user.getUser_id()));
        this.usersview_textField_username.setText(user.getUsername());
        this.usersview_textField_userFullname.setText(user.getFullname());
        this.usersview_textField_userEmail.setText(user.getEmail());
        this.usersview_textField_userPhoneNumber.setText(user.getPhone_number());
        this.usersview_comboBox_userType.setValue(user.getUser_type());
    }

    private void changeTask(){
        Task<Message> changeInfo = new Task<>() {
            @Override
            protected Message call() {
                if (selectUser == null || selectUser.getUser_id() != Integer.parseInt(usersview_textField_userId.getText())){
                    return new Message(Message.Status.FAIL, "user information error");
                }
//                System.out.println(selectUser);
                selectUser.setUser_type(usersview_comboBox_userType.getValue().trim().toLowerCase());
                selectUser.setEmail(usersview_textField_userEmail.getText());
                selectUser.setPhone_number(usersview_textField_userPhoneNumber.getText());
                selectUser.setFullname(usersview_textField_userFullname.getText());
                if (userDAO.update(selectUser)){
                    selectUser = null;
                    return new Message(Message.Status.SUCCESS, "change user info successfully");

                }else {
                    selectUser = null;
                    return new Message(Message.Status.FAIL, "change user info failed");
                }
            }
        };

        changeInfo.setOnSucceeded(event -> {
            Message returnMsg = changeInfo.getValue();
            Toast.show(returnMsg, this.currentStage);
            usersview_anchorPane_loadingPaneRoot.setVisible(false);
            enabledComponents();
            if (returnMsg.getStatus() == Message.Status.SUCCESS){
                getAllUsers();
            }
            if (returnMsg.getStatus() != Message.Status.SUCCESS && returnMsg.getStatus() != Message.Status.FAIL) {
                // Invalid status!!!
                throw new RuntimeException("Invalid status!!!");
            }
        });

        changeInfo.setOnFailed(event -> {
            Exception exception = (Exception) event.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), this.currentStage);
            enabledComponents();
        });

        new Thread(changeInfo).start();
    }

    private void deleteUser(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete user");
        alert.setHeaderText("Do your want to delete user '" + this.usersview_textField_username.getText() + "' ?");

        // Create custom ButtonTypes
        ButtonType okButton = new ButtonType("Yes");
        ButtonType cancelButton = new ButtonType("No");

        // Set the alert's button types
        alert.getButtonTypes().setAll(okButton, cancelButton);

        // Capture the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // If user chooses OK, continue; otherwise, return and don't download
        if (result.isPresent() && result.get() == okButton) {
            disabledComponents();
            deleteTask();
        }
    }

    private void deleteTask(){

        Task<Message> deleteUser = new Task<>() {
            @Override
            protected Message call() {
                if (userDAO.delete(Integer.parseInt(usersview_textField_userId.getText()))){
                    return new Message(Message.Status.SUCCESS, "delete user info successfully");

                }else {
                    return new Message(Message.Status.FAIL, "delete user info failed");
                }
            }
        };

        deleteUser.setOnSucceeded(event -> {
            Message returnMsg = deleteUser.getValue();
            Toast.show(returnMsg, this.currentStage);
            usersview_anchorPane_loadingPaneRoot.setVisible(false);
            enabledComponents();
            if (returnMsg.getStatus() == Message.Status.SUCCESS){
                getAllUsers();
            }else if (returnMsg.getStatus() != Message.Status.FAIL) {
                throw new RuntimeException("Invalid status!!!");
            }
        });

        deleteUser.setOnFailed(event -> {
            Exception exception = (Exception) event.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), this.currentStage);
            enabledComponents();
        });

        new Thread(deleteUser).start();
    }

    private  void clearTextContent(){
        this.usersview_textField_userId.setText("");
        this.usersview_textField_username.setText("");
        this.usersview_textField_userFullname.setText("");
        this.usersview_textField_userEmail.setText("");
        this.usersview_textField_userPhoneNumber.setText("");
        this.usersview_comboBox_userType.setValue("");
    }

    private void disabledComponents() {

        this.usersview_anchorPane_userDetailPaneRoot.setVisible(false);
        this.usersview_anchorPane_loadingPaneRoot.setVisible(true);

        // set parent scene button components
        if (this.parentsSceneController != null) {
            UserHomeController controller = (UserHomeController) this.parentsSceneController;
            controller.disabledAllUserHomeButtons();
        }
    }

    private void enabledComponents() {

        this.usersview_anchorPane_userDetailPaneRoot.setVisible(false);
        this.usersview_anchorPane_loadingPaneRoot.setVisible(false);

        // set parent scene button components
        if (this.parentsSceneController != null) {
            UserHomeController controller = (UserHomeController) this.parentsSceneController;
            controller.enabledAllUserHomeButtons();
        }
    }

}
