package vsas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
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
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UploadHistoryController implements SceneController {
    private SceneController parentSceneController;
    private Stage currentStage;
    private final DigitalScrollDAO digitalScrollDAO = DigitalScrollDAO.getInstance();
    private static boolean hasNewFile = false;

    private static String originalScrollName = null;
    private File selectedFile;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane uploadView_anchorpane_dialogPaneRoot;

    @FXML
    private Button uploadView_button_refresh;

    @FXML
    private Button uploadView_button_search;

    @FXML
    private Label uploadView_label_loadData;

    @FXML
    private Label uploadView_label_totalSearchnum;

    @FXML
    private ProgressBar uploadView_progressbar_loadData;

    @FXML
    private TableColumn<DigitalScroll, Integer> uploadView_tableitem_id;

    @FXML
    private TableColumn<DigitalScroll, String> uploadView_tableitem_name;

    @FXML
    private TableColumn<DigitalScroll, Long> uploadView_tableitem_size;

    @FXML
    private TableColumn<DigitalScroll, Timestamp> uploadView_tableitem_uploadTime;

    @FXML
    private TableView<DigitalScroll> uploadView_tableview_uploadHistory;

    @FXML
    private Text uploadView_text_title;

    @FXML
    private TextField uploadView_textfield_searchBox;

    @FXML
    private AnchorPane uploadview_anchorpane_downloadPane;

    @FXML
    private Button uploadview_button_dialogCancel;

    @FXML
    private Button uploadview_button_dialogSave;

    @FXML
    private Button uploadview_button_dialogSelect;

    @FXML
    private Button uploadview_button_dialogDelete;

    @FXML
    private DialogPane uploadview_dialogpane_scrollDetail;

    @FXML
    private GridPane uploadview_grippane_mainTable;

    @FXML
    private Label uploadview_label_downloadLabel;

    @FXML
    private AnchorPane uploadview_pane_dialogMain;

    @FXML
    private Pane uploadview_pane_overlay;

    @FXML
    private ProgressBar uploadview_progressBar_downloadProgress;

    @FXML
    private TextField uploadview_textField_dialogCreateTime;

    @FXML
    private TextArea uploadview_textField_dialogDescription;

    @FXML
    private TextField uploadview_textField_dialogDownloadTimes;

    @FXML
    private TextField uploadview_textField_dialogFileName;

    @FXML
    private TextField uploadview_textField_dialogFileSize;

    @FXML
    private TextField uploadview_textField_dialogID;

    @FXML
    private TextField uploadview_textField_dialogName;

    @FXML
    private TextField uploadview_textField_dialogUpdateTime;

    @FXML
    private TextField uploadview_textField_dialogUploadTimes;

    @FXML
    private Text uploadview_text_dialogCreateTime;

    @FXML
    private Text uploadview_text_dialogDescription;

    @FXML
    private Text uploadview_text_dialogDownloadTimes;

    @FXML
    private Text uploadview_text_dialogFileName;

    @FXML
    private Text uploadview_text_dialogFileSize;

    @FXML
    private Text uploadview_text_dialogID;

    @FXML
    private Text uploadview_text_dialogName;

    @FXML
    private Text uploadview_text_dialogUpdateTime;

    @FXML
    private Text uploadview_text_dialogUploadTimes;

    @FXML
    void initialize() {

        Label placeholderLabel = new Label("The data in the table is empty");
        uploadView_tableview_uploadHistory.setPlaceholder(placeholderLabel);

        // disabled
        this.uploadView_anchorpane_dialogPaneRoot.setVisible(false);

        // get local data
        List<DigitalScroll> scrolls = LocalStorage.getInstance().getUploadedScrollList();

        // if local data not exist
        if (scrolls.isEmpty()) {

            // loading data disabled components
            disabledComponents();

            // new thread to get data
            getAllScrollData();
        }
        else {
            Toast.show(new Message(Message.Status.SUCCESS, "Local cache load succeed"), this.currentStage);

            // load cache data
            setTableItems(scrolls);

            // set the result label
            uploadView_label_totalSearchnum.setText("Total "+ LocalStorage.getInstance().getUploadedScrollList().size() +" results");

        }

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
                DigitalScroll selectedScroll = (DigitalScroll) row.getItem();
                // reset
                hasNewFile = false;
                originalScrollName = selectedScroll.getScroll_name();
                // Open the sub-pane here
                openDialogPane(selectedScroll);
            }
        }
    }

    private void openDialogPane(DigitalScroll selectedScroll) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.uploadview_textField_dialogID.setText(String.valueOf(selectedScroll.getScroll_id()));
        this.uploadview_textField_dialogName.setText(selectedScroll.getScroll_name());
        this.uploadview_textField_dialogFileName.setText(selectedScroll.getFile_name());
        this.uploadview_textField_dialogCreateTime.setText(sdf.format(selectedScroll.getCreate_time()));
        this.uploadview_textField_dialogUpdateTime.setText(sdf.format(selectedScroll.getUpdate_time()));
        this.uploadview_textField_dialogUploadTimes.setText(String.valueOf(selectedScroll.getUpload_times()));
        this.uploadview_textField_dialogDownloadTimes.setText(String.valueOf(selectedScroll.getDownload_times()));
        this.uploadview_textField_dialogFileSize.setText(String.valueOf(selectedScroll.getFile_size()));
        this.uploadview_textField_dialogDescription.setText(selectedScroll.getScroll_description());

        // open dialog pane
        this.uploadView_anchorpane_dialogPaneRoot.setVisible(true);

        // change the button visible
        this.uploadview_button_dialogSave.setVisible(true);
        this.uploadview_button_dialogCancel.setVisible(true);

    }

    private void closeDialogPane(){
        // close dialog pane
        this.uploadView_anchorpane_dialogPaneRoot.setVisible(false);
    }

    //fill data into UI table view
    private void setTableItems(List<DigitalScroll> scrolls) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        uploadView_tableitem_id.setCellValueFactory(new PropertyValueFactory<>("scroll_id"));
        uploadView_tableitem_name.setCellValueFactory(new PropertyValueFactory<>("scroll_name"));
        uploadView_tableitem_size.setCellValueFactory(new PropertyValueFactory<>("file_size"));
        uploadView_tableitem_size.setCellFactory(column -> new TableCell<>() {
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    // Convert bytes to KB and format the value
                    setText(String.format("%.2f KB", item / 1024.0));
                }
            }
        });
        uploadView_tableitem_uploadTime.setCellValueFactory(new PropertyValueFactory<>("update_time"));
        uploadView_tableitem_uploadTime.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(sdf.format(item));
                }
            }
        });
        ObservableList<DigitalScroll> data = FXCollections.observableArrayList(scrolls);
        uploadView_tableview_uploadHistory.setItems(data);
    }
    
    private void getAllScrollData() {

        // create a task
        Task<Message> getScrolls = new Task<>() {
            @Override
            protected Message call() {

                // get data from database
                List<DigitalScroll> pojoList = digitalScrollDAO.selectUploadScrolls(LocalStorage.getInstance().getUser_id());

                digitalScrollDAO.restartConnection();

                // store the data to the local
                LocalStorage.getInstance().setUploadedScrollList(pojoList);
                List<DigitalScroll> uploadedScrollList = LocalStorage.getInstance().getUploadedScrollList();

                // print the result
                if (uploadedScrollList.isEmpty()) {
                    return new Message(Message.Status.FAIL, "Data get failed or you haven't upload anything, try refresh");
                } else {
                    return new Message(Message.Status.SUCCESS, "Data get succeed");
                }
            }
        };
        getScrolls.setOnSucceeded(e -> {

            Message returnMsg = getScrolls.getValue();
            Toast.show(returnMsg, this.currentStage);
            enabledComponents();

            // set result text
            uploadView_label_totalSearchnum.setText("Total "+ LocalStorage.getInstance().getUploadedScrollList().size() +" results");

            if (returnMsg.getStatus() == Message.Status.SUCCESS ) {
                // set table items
                setTableItems(LocalStorage.getInstance().getUploadedScrollList());
            }
        });

        getScrolls.setOnFailed(e -> {
            if (getScrolls.getValue() == null){
                Toast.show(new Message(Message.Status.FAIL, "The system is busy, please try again later. "), this.currentStage);
            }else {
                Toast.show(getScrolls.getValue(), this.currentStage);
            }

            // enabled components
            enabledComponents();
        });

        new Thread(getScrolls).start();

    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    private void enabledComponents() {
        this.uploadView_label_loadData.setVisible(false);
        this.uploadView_progressbar_loadData.setVisible(false);
        this.uploadView_tableview_uploadHistory.setDisable(false);
        this.uploadView_button_search.setDisable(false);
        this.uploadView_button_refresh.setDisable(false);
        this.uploadView_textfield_searchBox.setDisable(false);

        // set parent scene button components
        if (this.parentSceneController != null){
            UserHomeController controller = (UserHomeController)this.parentSceneController;
            controller.enabledAllUserHomeButtons();
        }

    }

    private void disabledComponents() {
        this.uploadView_label_loadData.setVisible(true);
        this.uploadView_progressbar_loadData.setVisible(true);
        this.uploadView_tableview_uploadHistory.setDisable(true);
        this.uploadView_button_search.setDisable(true);
        this.uploadView_button_refresh.setDisable(true);
        this.uploadView_textfield_searchBox.setDisable(true);

        // set parent scene button components
        if (this.parentSceneController != null){
            UserHomeController controller = (UserHomeController)this.parentSceneController;
            controller.disabledAllUserHomeButtons();
        }

    }

    private void disabledAllDialogTxtField(){
        this.uploadview_textField_dialogName.setDisable(true);
        this.uploadview_textField_dialogDescription.setDisable(true);
    }

    @FXML
    void onDialogCancelAction(ActionEvent event) {
        closeDialogPane();
        enabledComponents();
    }

    @FXML
    void onDialogOverlayAction(MouseEvent event) {
        closeDialogPane();
        enabledComponents();
    }

    @FXML
    void onDialogSelectScrollAction(ActionEvent event) {
        // TODO
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
                return;
            }

            if (fileSize <= DEFINE.MAX_UPLOAD_SIZE){
                this.uploadview_textField_dialogFileName.setText(fileName);
                this.uploadview_textField_dialogFileSize.setText(String.valueOf(fileSize));
                hasNewFile = true;
            }else {
                Toast.show( new Message(Message.Status.WARNING, "upload scroll size over 15 MB"),currentStage);
            }
        }else {
            Toast.show( new Message(Message.Status.FAIL, "No file was selected"),currentStage);
        }
    }

    @FXML
    void onDialogSaveAction(ActionEvent event) {
        // TODO
        // scroll name can not be null or empty
        String scrollName = this.uploadview_text_dialogName.getText();
        if (scrollName == null || scrollName.isEmpty()){
            Toast.show(new Message(Message.Status.WARNING, "scrollName can not be null or empty!"), this.currentStage);
            return;
        }
        if (hasNewFile){
            disabledComponents();
            hasNewScrollChange();
        }else {
            disabledComponents();
            noNewScrollChange();
        }
    }

    @FXML
    void onDialogDeleteAction(ActionEvent event){
        disabledComponents();
        deleteUserScroll();
    }
    @FXML
    void onRefreshAction(ActionEvent event) {
        disabledComponents();
        getAllScrollData();
    }

    @FXML
    void onuploadHistorySearchAction(ActionEvent event) {
        disabledComponents();
        String searchContent = this.uploadView_textfield_searchBox.getText().trim().toLowerCase();
        List<DigitalScroll> searchScrolls = new ArrayList<>();
        List<DigitalScroll> localScrolls = LocalStorage.getInstance().getUploadedScrollList();

        if (searchContent.isEmpty()){
            this.uploadView_label_totalSearchnum.setText("Total "+ localScrolls.size() +" results");

            // set table view's items
            setTableItems(localScrolls);

        }else {
            // find relative scrolls
            for (DigitalScroll ds: localScrolls) {
                if (ds.getScroll_name().toLowerCase().contains(searchContent)){
                    searchScrolls.add(ds);
                }
            }
            this.uploadView_label_totalSearchnum.setText("Total "+ searchScrolls.size() +" results");

            // set table view's items
            setTableItems(searchScrolls);
        }
        enabledComponents();
    }

    @Override
    public void setParentsSceneController(SceneController parentsSceneController) {
        this.parentSceneController = parentsSceneController;
    }

    private void deleteUserScroll(){

        Task<Message> deleteTask = new Task<>() {
            @Override
            protected Message call() {
                digitalScrollDAO.restartConnection();
                int scroll_id = Integer.parseInt(uploadview_textField_dialogID.getText());
                if (digitalScrollDAO.delete(scroll_id)) {
                    return new Message(Message.Status.SUCCESS, "Delete succeed");
                } else {
                    return new Message(Message.Status.FAIL, "Delete failed");
                }
            }
        };

        deleteTask.setOnSucceeded(e -> {
            Toast.show(deleteTask.getValue(), this.currentStage);
            enabledComponents();
            uploadView_anchorpane_dialogPaneRoot.setVisible(false);
            if (deleteTask.getValue().getStatus() == Message.Status.SUCCESS){
                getAllScrollData();
            }
        });

        deleteTask.setOnFailed(e -> {
            Exception exception = (Exception) e.getSource().getException();
            exception.printStackTrace();
            enabledComponents();
            uploadView_anchorpane_dialogPaneRoot.setVisible(false);
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), this.currentStage);
        });

        new Thread(deleteTask).start();
    }

    private void noNewScrollChange(){
        Task<Message> noScrollChangeTask = new Task<>() {
            @Override
            protected Message call() {
                digitalScrollDAO.restartConnection();

                // if scroll exist
                String textFieldScrollName = uploadview_textField_dialogName.getText();
                if (!textFieldScrollName.equals(originalScrollName)){
                    if(digitalScrollDAO.scrollExist(textFieldScrollName)){
                        return new Message(Message.Status.FAIL, "The same name scroll already exists");
                    }
                }
                // set properties of the scroll
                DigitalScroll scroll = new DigitalScroll();
//                System.out.println("uploadview_text_dialogID.getText() :" + uploadview_textField_dialogID.getText());
                scroll.setScroll_id(Integer.parseInt(uploadview_textField_dialogID.getText()));
                scroll.setScroll_description(uploadview_textField_dialogDescription.getText());
                scroll.setScroll_name(uploadview_textField_dialogName.getText());

                // Upload the byte array to the database
                if ( digitalScrollDAO.updateWithoutScroll(scroll)) {
                    return new Message(Message.Status.SUCCESS, "File uploaded successfully");
                } else {
                    return new Message(Message.Status.FAIL, "Failed to upload file to database");
                }
            }
        };

        noScrollChangeTask.setOnSucceeded(event -> {
            Message returnMsg = noScrollChangeTask.getValue();
            Toast.show(returnMsg, this.currentStage);
            enabledComponents();
            getAllScrollData();
            uploadView_anchorpane_dialogPaneRoot.setVisible(false);

            if (returnMsg.getStatus() == Message.Status.SUCCESS){

            }else if (returnMsg.getStatus() != Message.Status.FAIL) {
                throw new RuntimeException("Invalid status!!!");
            }
        });

        noScrollChangeTask.setOnFailed(event -> {
            Exception exception = (Exception) event.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), this.currentStage);
            enabledComponents();
        });

        new Thread(noScrollChangeTask).start();
    }

    private void hasNewScrollChange(){
        Task<Message> newScrollChangeTask = new Task<>() {
            @Override
            protected Message call() {

                //Check the type of upload file
                if (selectedFile == null || !selectedFile.getName().endsWith(".txt")) {
                    hasNewFile = false;
                    return new Message(Message.Status.FAIL, "Wrong file type uploaded");
                }
                // if scroll exist
                String textFieldScrollName = uploadview_textField_dialogName.getText();
                if (!textFieldScrollName.equals(originalScrollName)){
                    if(digitalScrollDAO.scrollExist(textFieldScrollName)){
                        return new Message(Message.Status.FAIL, "The same name scroll already exists");
                    }
                }

                digitalScrollDAO.restartConnection();

                // upload scroll
                // Convert the FileInputStream to a byte array
                try (FileInputStream fis = new FileInputStream(selectedFile)) {
                    byte[] fileData = fis.readAllBytes();

                    // set properties of the scroll
                    DigitalScroll scroll = new DigitalScroll();
                    scroll.setScroll_id(Integer.parseInt(uploadview_textField_dialogID.getText()));
                    scroll.setBinary_file(fileData);
                    scroll.setUpload_times(Integer.parseInt(uploadview_textField_dialogUploadTimes.getText()) + 1);
                    scroll.setScroll_description(uploadview_textField_dialogDescription.getText());
                    scroll.setScroll_name(uploadview_textField_dialogName.getText());
                    scroll.setFile_name(uploadview_textField_dialogFileName.getText());
                    scroll.setFile_size(Integer.parseInt(uploadview_textField_dialogFileSize.getText()));

                    // Upload the byte array to the database
                    if (digitalScrollDAO.updateWithScroll(scroll)) {
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

        newScrollChangeTask.setOnSucceeded(event -> {
            Message returnMsg = newScrollChangeTask.getValue();
            Toast.show(returnMsg, this.currentStage);
            enabledComponents();
            getAllScrollData();
            uploadView_anchorpane_dialogPaneRoot.setVisible(false);
            if (returnMsg.getStatus() != Message.Status.FAIL &&returnMsg.getStatus() != Message.Status.SUCCESS ) {
                throw new RuntimeException("Invalid status!!!");
            }
        });

        newScrollChangeTask.setOnFailed(event -> {
            Exception exception = (Exception) event.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), this.currentStage);
            enabledComponents();
        });

        new Thread(newScrollChangeTask).start();
    }
}
