package vsas.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vsas.component.Toast;
import vsas.dao.DigitalScrollDAO;
import vsas.localstorage.LocalStorage;
import vsas.pojo.DigitalScroll;
import vsas.pojo.Message;
import vsas.utils.DEFINE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UploadScrollController implements SceneController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane uploadScroll_AnchorPane_ROOT;

    @FXML
    private AnchorPane uploadScroll_AnchorPane_dialogRoot;

    @FXML
    private AnchorPane uploadScroll_AnchorPane_uploadDialog;

    @FXML
    private Button uploadScroll_Button_selectFile;

    @FXML
    private Pane uploadScroll_Pane_overlay;

    @FXML
    private ProgressBar uploadScroll_ProgressBar_upload;

    @FXML
    private Button uploadScroll_button_upload;

    @FXML
    private Label uploadScroll_label_uploadText;

    @FXML
    private TextArea uploadScroll_textArea_description;

    @FXML
    private Label uploadScroll_label_fileName;

    @FXML
    private Label uploadScroll_textField_fileSize;

    @FXML
    private TextField uploadScroll_textField_scrollName;

    private UserHomeController parentsSceneController;

    private Stage currentStage;

    private File selectedFile;

    @FXML
    void onSelectFile(ActionEvent event) {

        disableSelectFile();

        // select upload file
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        selectedFile = fileChooser.showOpenDialog(currentStage);


        if (selectedFile != null){
            String fileName = selectedFile.getName();
            long fileSize = selectedFile.length();

            if (!fileName.endsWith(".txt")) {
                Toast.show(new Message(Message.Status.FAIL, "Wrong file type uploaded"), currentStage);
                clearUploadScroll();
                enableSelectFile();
                return;
            }

            if (fileSize <= DEFINE.MAX_UPLOAD_SIZE){
                this.uploadScroll_label_fileName.setText(fileName);
                this.uploadScroll_textField_fileSize.setText(String.valueOf(fileSize));
            }else {
                Toast.show( new Message(Message.Status.WARNING, "upload scroll size over 15 MB"),currentStage);
                clearUploadScroll();
            }
        }else {
            Toast.show( new Message(Message.Status.FAIL, "No file was selected"),currentStage);
        }

        enableSelectFile();
    }

    DigitalScrollDAO digitalScrollDAO = DigitalScrollDAO.getInstance();
    private Task<Message> uploadFile() {
        return new Task<>() {
            @Override
            protected Message call() {
                //Check the type of upload file
                if (selectedFile == null || !selectedFile.getName().endsWith(".txt")) {
                    return new Message(Message.Status.FAIL, "Wrong file type uploaded");
                }
                // if scroll exist
                if (uploadScroll_textField_scrollName.getText() == null || uploadScroll_textField_scrollName.getText().isEmpty()){
                    return new Message(Message.Status.FAIL, "The scroll name error");
                }
                if(digitalScrollDAO.scrollExist(uploadScroll_textField_scrollName.getText())){
                    return new Message(Message.Status.FAIL, "The same name scroll already exists");
                }

                if (selectedFile == null) return new Message(Message.Status.FAIL, "upload file is not exist");

                digitalScrollDAO.restartConnection();

                // upload scroll
                // Convert the FileInputStream to a byte array
                try (FileInputStream fis = new FileInputStream(selectedFile)) {
                    byte[] fileData = fis.readAllBytes();

                    // set properties of the scroll
                    DigitalScroll scroll = new DigitalScroll();
                    scroll.setBinary_file(fileData);
                    scroll.setUploader_id(LocalStorage.getInstance().getUser_id());
                    scroll.setScroll_description(uploadScroll_textArea_description.getText());
                    scroll.setScroll_name(uploadScroll_textField_scrollName.getText());
                    scroll.setFile_name(uploadScroll_label_fileName.getText());
                    scroll.setFile_size(Integer.parseInt(uploadScroll_textField_fileSize.getText()));

                    // Upload the byte array to the database
                    if (digitalScrollDAO.create(scroll)) {

                        return new Message(Message.Status.SUCCESS, "File uploaded successfully");

                    } else {
                        return new Message(Message.Status.FAIL, "Failed to upload file to database");
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                    return new Message(Message.Status.FAIL, "Error reading file: " + e.getMessage());
                }
            }
        };
    }

    @FXML
    void onUploadAction(ActionEvent event) {
        //disabled
        disabledUploadFile();

        // check upload scroll
        if (selectedFile == null
                || this.uploadScroll_textField_fileSize.getText().isEmpty()
                || this.uploadScroll_textField_scrollName.getText().isEmpty()
                || this.uploadScroll_label_fileName.getText().isEmpty()){
            Toast.show(new Message(Message.Status.INFO, "Please select a valid scroll"), currentStage);
            enabledUploadFile();
            return;
        }

         // setting upload task
        Task<Message> uploadFile = uploadFile();

        // Task succeeded
        uploadFile.setOnSucceeded(e -> {
            Toast.show(uploadFile.getValue(), currentStage);
            clearUploadScroll();
            enabledUploadFile();
        });

        // Task Failed
        uploadFile.setOnFailed(e -> {
            Exception exception = (Exception) e.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()),currentStage);
            clearUploadScroll();
            enabledUploadFile();
        });

        new Thread(uploadFile).start();
    }

    @FXML
    void initialize() {
        // filename and size can not be edited
        this.uploadScroll_textField_scrollName.setDisable(false);
        this.uploadScroll_textField_fileSize.setDisable(false);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    @Override
    public void setParentsSceneController(SceneController parentsSceneController) {
        this.parentsSceneController = (UserHomeController) parentsSceneController;
    }

    private void disabledUploadFile(){
        disableSelectFile();
        this.uploadScroll_textField_scrollName.setDisable(true);
        this.uploadScroll_AnchorPane_dialogRoot.setVisible(true);
    }

    private void enabledUploadFile(){
        enableSelectFile();
        this.uploadScroll_textField_scrollName.setDisable(false);
        this.uploadScroll_AnchorPane_dialogRoot.setVisible(false);
    }

    private void disableSelectFile(){
        disabledAllButtonOnUserHomePage();
        this.uploadScroll_Button_selectFile.setDisable(true);
        this.uploadScroll_button_upload.setDisable(true);
    }

    private void enableSelectFile(){
        enabledAllButtonOnUserHomePage();
        this.uploadScroll_Button_selectFile.setDisable(false);
        this.uploadScroll_button_upload.setDisable(false);
    }

    private void disabledAllButtonOnUserHomePage() {
        if (this.parentsSceneController != null) {
            this.parentsSceneController.disabledAllUserHomeButtons();
        }
    }

    private void enabledAllButtonOnUserHomePage() {
        if (this.parentsSceneController != null) {
            this.parentsSceneController.enabledAllUserHomeButtons();
        }
    }

    private  void clearUploadScroll(){
        this.selectedFile = null;
        this.uploadScroll_textField_fileSize.setText("Null");
        this.uploadScroll_label_fileName.setText("Null");
        this.uploadScroll_textField_scrollName.setText("");
        this.uploadScroll_textArea_description.setText("");
    }
}
