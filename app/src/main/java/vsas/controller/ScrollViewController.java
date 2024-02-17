package vsas.controller;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import vsas.component.Toast;
import vsas.dao.DigitalScrollDAO;
import vsas.dao.UserDAO;
import vsas.dao.UserDownloadDAO;
import vsas.localstorage.LocalStorage;
import vsas.pojo.DigitalScroll;
import vsas.pojo.Message;
import vsas.pojo.Pojo;
import vsas.pojo.UserDownload;
import vsas.utils.DEFINE;

public class ScrollViewController implements SceneController {

    private final DigitalScrollDAO digitalScrollDAO = DigitalScrollDAO.getInstance();
    private SceneController parentsSceneController;
    private Stage currentStage;
    private final UserDownloadDAO userDownloadDAO = UserDownloadDAO.getInstance();
    private final UserDAO userDao = UserDAO.getInstance();
    private int scrollClickedID;

    private DigitalScroll scrollClicked;

    @Override
    public void setParentsSceneController(SceneController parentsSceneController) {
        this.parentsSceneController = parentsSceneController;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane scrollview_anchorpane_dialogPaneRoot;

    @FXML
    private AnchorPane scrollview_anchorpane_downloadPane;

    @FXML
    private Button scrollview_button_dialogCancel;

    @FXML
    private Button scrollview_button_dialogDelete;

    @FXML
    private Button scrollview_button_dialogDownload;

    @FXML
    private Button scrollview_button_dialogSave;

    @FXML
    private Button scrollview_button_refresh;

    @FXML
    private Button scrollview_button_search;
    @FXML
    private ComboBox<String> scrollview_combobox_search;

    @FXML
    private DialogPane scrollview_dialogpane_scrollDetail;

    @FXML
    private GridPane scrollview_grippane_mainTable;

    @FXML
    private Label scrollview_label_downloadLabel;

    @FXML
    private Label scrollview_label_loadData;

    @FXML
    private Label scrollview_label_searchResult;

    @FXML
    private AnchorPane scrollview_pane_dialogMain;

    @FXML
    private Pane scrollview_pane_overlay;

    @FXML
    private ProgressBar scrollview_progressBar_downloadProgress;

    @FXML
    private ProgressBar scrollview_progressbar_loadData;

    @FXML
    private TableColumn<DigitalScroll, Timestamp> scrollview_tableitem_createTime;

    @FXML
    private TableColumn<DigitalScroll, Integer> scrollview_tableitem_downloadTimes;

    @FXML
    private TableColumn<DigitalScroll, Integer> scrollview_tableitem_id;

    @FXML
    private TableColumn<DigitalScroll, String> scrollview_tableitem_name;

    @FXML
    private TableColumn<DigitalScroll, String> scrollview_tableitem_uploaderName;

    @FXML
    private TableView<DigitalScroll> scrollview_tableview_scrollList;

    @FXML
    private Label scrollview_textField_dialogCreateTime;

    @FXML
    private TextArea scrollview_textField_dialogDescription;

    @FXML
    private Label scrollview_textField_dialogDownloadTimes;

    @FXML
    private Label scrollview_textField_dialogFileName;

    @FXML
    private Label scrollview_textField_dialogFileSize;

    @FXML
    private Label scrollview_textField_dialogID;

    @FXML
    private Label scrollview_textField_dialogName;

    @FXML
    private Label scrollview_textField_dialogUpdateTime;

    @FXML
    private Label scrollview_textField_dialogUploadTimes;

    @FXML
    private Label scrollview_textField_dialogUploader;

    @FXML
    private Label scrollview_textField_dialogUploaderName;

    @FXML
    private Text scrollview_text_dialogCreateTime;

    @FXML
    private Text scrollview_text_dialogDescription;

    @FXML
    private Text scrollview_text_dialogDownloadTimes;

    @FXML
    private Text scrollview_text_dialogFileSize;

    @FXML
    private Text scrollview_text_dialogID;

    @FXML
    private Text scrollview_text_dialogName;

    @FXML
    private Text scrollview_text_dialogName1;

    @FXML
    private Text scrollview_text_dialogUpdateTime;

    @FXML
    private Text scrollview_text_dialogUploadTimes;

    @FXML
    private Text scrollview_text_dialogUploader;

    @FXML
    private Text scrollview_text_dialogUploader1;

    @FXML
    private Text scrollview_text_title;

    @FXML
    private TextField scrollview_textfield_searchBox;

    int getScrollClickedID(){return this.scrollClickedID;}
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

                int selectedId = selectedScroll.getScroll_id();
                System.out.println("Selected ID: " + selectedId);
                this.scrollClickedID = selectedId;
                this.scrollClicked = selectedScroll;



                // Open the sub-pane here
                openDialogPane(selectedScroll);
            }
        }
    }

    @FXML
    void onDialogOverlayAction(MouseEvent event) {
        closeDialogPane();
        enabledComponents();
    }

    @FXML
    void onDialogCancelAction(ActionEvent event) {
        closeDialogPane();
        enabledComponents();
    }

    @FXML
    void onDialogDownloadAction(ActionEvent event) {

        String scrollID = this.scrollview_textField_dialogID.getText();

        if (scrollID.isEmpty()){
            Toast.show(new Message(Message.Status.FAIL, "Unknown error please try refreshing the page!"), currentStage);
            closeDialogPane();
        }else {
            int downloadScrollID = Integer.parseInt(scrollID);

            // disabled components
            downloadDisableComponents();

            File selectedDirectory;
            // get download path
            if (LocalStorage.getInstance().isTestMode()) {
                String currentWorkingDirectory = System.getProperty("user.dir");
                selectedDirectory = new File(currentWorkingDirectory);
            } else {
                // DirectoryChooser
                DirectoryChooser directoryChooser = new DirectoryChooser();
                selectedDirectory = directoryChooser.showDialog(currentStage);
            }

            if (selectedDirectory != null){

                // create download file in user's dist
                String scrollName = scrollview_textField_dialogFileName.getText();
                File outputFile = new File(selectedDirectory, scrollName);

                // Check if file already exists
                if (outputFile.exists()) {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("File Exists");
                    alert.setHeaderText("The file already exists.");

                    // Create custom ButtonTypes
                    ButtonType okButton = new ButtonType("Continue");
                    ButtonType cancelButton = new ButtonType("Cancel");

                    // Set the alert's button types
                    alert.getButtonTypes().setAll(okButton, cancelButton);

                    // Capture the user's response
                    Optional<ButtonType> result = alert.showAndWait();

                    // If user chooses OK, continue; otherwise, return and don't download
                    if (result.isPresent() && result.get() == okButton) {
                        Toast.show(new Message(Message.Status.WARNING, "Making a new copy"), currentStage);

                        // Append a timestamp to the filename
                        String fileExtension = "";
                        int dotIndex = scrollName.lastIndexOf('.');
                        if (dotIndex > 0 && dotIndex < scrollName.length() - 1) {
                            fileExtension = scrollName.substring(dotIndex);
                            scrollName = scrollName.substring(0, dotIndex);
                        }
                        String timestamp = String.valueOf(System.currentTimeMillis());
                        scrollName = scrollName + "_" + timestamp + fileExtension;
                        outputFile = new File(selectedDirectory, scrollName);

                    }else {
                        // User chose CANCEL or closed the dialog
                        Toast.show(new Message(Message.Status.INFO, "Download cancelled by user."), currentStage);
                        downloadEnableComponents();
                        return; // Exit the method without downloading
                    }
                }

                // set task
                Task<Message> downloadScroll = createDownloadTask(outputFile, downloadScrollID);

                // task succeed
                downloadScroll.setOnSucceeded(e -> {

                    // refresh the table
                    onRefreshAction(event);

                    Message returnMsg = downloadScroll.getValue();
                    Toast.show(returnMsg, this.currentStage);
                    downloadEnableComponents();

                    // close downloadPane
                    this.scrollview_anchorpane_downloadPane.setVisible(false);

                    if (returnMsg.getStatus() != Message.Status.SUCCESS && returnMsg.getStatus() != Message.Status.FAIL) {
                        // Invalid status!!!
                        throw new RuntimeException("Invalid status!!!");
                    }
                });

                // Task failed
                downloadScroll.setOnFailed(e -> {

                    // This will print the stack trace of the exception
                    Throwable exception = e.getSource().getException();
                    exception.printStackTrace();

                    if (downloadScroll.getValue() == null){

                        Toast.show(new Message(Message.Status.FAIL,exception.getMessage()), this.currentStage);
                    }else {
                        Toast.show(downloadScroll.getValue(), this.currentStage);
                    }

                    // enabled components
                    this.scrollview_anchorpane_downloadPane.setVisible(false);
                    downloadEnableComponents();
                });

                // create a thread to execute the task
                new Thread(downloadScroll).start();

            }else {

                // enabled components
                downloadEnableComponents();

                Toast.show(new Message(Message.Status.FAIL, "Can not find the folder, please try again!"), currentStage);
            }
        }
    }

    @FXML
    void onDialogDeleteAction(ActionEvent event){
    }

    @FXML
    void onDialogSaveAction(ActionEvent event){

    }
    //TODO download file here
    private Task<Message> createDownloadTask(File selectedDirectory, int downloadScrollID) {
        return new Task<>() {
            @Override
            protected Message call() {

                // open the download progressBar
                scrollview_anchorpane_downloadPane.setVisible(true);

                // get inputStream from server
                InputStream blobStream = digitalScrollDAO.selectBlobStream(downloadScrollID);

                // can not get input stream from server
                if (blobStream == null) return new Message(Message.Status.FAIL, "Can not download data from server");

                // set a buffer to load data
                byte[] buffer = new byte[4096];
                int bytesRead;

                // open an output stream to transfer input stream data
                try (FileOutputStream fos = new FileOutputStream(selectedDirectory)) {
                    while ((bytesRead = blobStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }

                    // download all data from server, close the transfer stream
                    blobStream.close();

                    // add one to the download times
                    if (!digitalScrollDAO.addDownloadTimes(downloadScrollID)) return new Message(Message.Status.FAIL, "file download succeed, but download times add failed");

                    // add a download scroll history
                    UserDownload download = new UserDownload();
                    download.setDownloaderId(LocalStorage.getInstance().getUser_id());
                    download.setScrollId(downloadScrollID);
                    if (!userDownloadDAO.create(download)) return new Message(Message.Status.FAIL, "file download succeed, but download history add failed");

                } catch (IOException e) {


                    // get failed about the input or output stream
                    e.printStackTrace();
                    return new Message(Message.Status.FAIL, e.getMessage());
                }

                // get all data
                return new Message(Message.Status.SUCCESS, "download file successfully");
            }
        };
    }

    @FXML
    void onScrollSearchAction(ActionEvent event) {
        disabledComponents();
        String searchContent = this.scrollview_textfield_searchBox.getText().trim().toLowerCase();
        List<DigitalScroll> searchScrolls = new ArrayList<>();
        List<DigitalScroll> localScrolls = LocalStorage.getInstance().getScrollList();
        String selectedItem = scrollview_combobox_search.getSelectionModel().getSelectedItem();

        if (searchContent.isEmpty()){
            searchScrolls = localScrolls;
        } else {
            switch (selectedItem) {
                case "ScrollName":
                    for (DigitalScroll ds: localScrolls) {
                        if (ds.getScroll_name().toLowerCase().contains(searchContent)) {
                            searchScrolls.add(ds);
                        }
                    }
                    break;
                case "ScrollID":
                    for (DigitalScroll ds: localScrolls) {
                        if (String.valueOf(ds.getScroll_id()).contains(searchContent)) {
                            searchScrolls.add(ds);
                        }
                    }
                    break;
                case "UploaderName":
                    // Assuming there's a getUploader_name() method
                    for (DigitalScroll ds: localScrolls) {
                        if (ds.getUploader_name().toLowerCase().contains(searchContent)) {
                            searchScrolls.add(ds);
                        }
                    }
                    break;
                case "UploaderID":
                    for (DigitalScroll ds: localScrolls) {
                        if (String.valueOf(ds.getUploader_id()).contains(searchContent)) {
                            searchScrolls.add(ds);
                        }
                    }
                    break;
                case "CreateTime":
                    for (DigitalScroll ds: localScrolls) {
                        if (String.valueOf(ds.getCreate_time()).contains(searchContent)) {
                            searchScrolls.add(ds);
                        }
                    }
                    break;
                case "All":
                default:
                    for (DigitalScroll ds: localScrolls) {
                        if (ds.getScroll_name().toLowerCase().contains(searchContent)
                                || String.valueOf(ds.getScroll_id()).contains(searchContent)
                            // Add more conditions here for other fields
                        ) {
                            searchScrolls.add(ds);
                        }
                    }
                    break;
            }
        }

        this.scrollview_label_searchResult.setText("Total "+ searchScrolls.size() +" results");
        setTableItems(searchScrolls);
        enabledComponents();
    }

    @FXML
    void onRefreshAction(ActionEvent event) {
        disabledComponents();
        getAllScrollData();
    }
    @FXML
    void onPreviewAction(ActionEvent event) throws Exception {
        boolean isTheRightScroll = false;
        for(DigitalScroll digitalScroll: digitalScrollDAO.selectUploadScrolls(LocalStorage.getInstance().getUser_id())) {
            if(digitalScroll.getScroll_id().equals(this.scrollClickedID)){
                isTheRightScroll = true;
                break;
            }
        }
        if(!isTheRightScroll){
            Toast.show(new Message(Message.Status.WARNING, "You can't edit the file you didn't upload"),currentStage);
            InputStream ipStreamNotEdit = digitalScrollDAO.selectBlobStream(this.scrollClickedID);
            try {
                String toShow = convertBlobToString(ipStreamNotEdit);
                enable_preview_component();
                prewview_textarea_showtext.setText(toShow);
                prewview_textarea_showtext.setVisible(true);
                prevew_button_dialogsave.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.show(new Message(Message.Status.WARNING, "File can't be previewed"), currentStage);
            }
        }else {
            InputStream ipStreamCanEdit = digitalScrollDAO.selectBlobStream(this.scrollClickedID);
            try {
                String toShow = convertBlobToString(ipStreamCanEdit);
                enable_preview_component();
                prewview_textarea_showtext.setText(toShow);
                prevew_button_dialogsave.setVisible(true);
                prewview_textarea_showtext.setVisible(true);
                prewview_textarea_showtext.setEditable(true);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.show(new Message(Message.Status.WARNING, "File can't be previewed"), currentStage);
            }
        }

    }
    public static String convertBlobToString(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[4096];
        int bytesRead;
        StringBuilder stringBuilder = new StringBuilder();

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, bytesRead));
        }

        inputStream.close();
        return stringBuilder.toString();
    }


    @FXML
    void initialize() {

        Label placeholderLabel = new Label("The data in the table is empty");
        scrollview_tableview_scrollList.setPlaceholder(placeholderLabel);
        scrollview_combobox_search.getSelectionModel().select("All");

        // disabled
        this.scrollview_anchorpane_dialogPaneRoot.setVisible(false);
        this.scrollview_anchorpane_downloadPane.setVisible(false);
        this.preview_anchorpane_dialogpaneroot.setVisible(false);
        this.preview_pane_overlay.setVisible(false);
        this.preview_pane_dialogmain.setVisible(false);
        this.preview_dialogpane_scrollID.setVisible(false);

        // get local data
        List<DigitalScroll> scrolls = LocalStorage.getInstance().getScrollList();

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
            scrollview_label_searchResult.setText("Total "+ LocalStorage.getInstance().getScrollList().size() +" results");

        }

        // disabledAllDialogTextField
        disabledAllDialogTxtField();
    }

    private void openDialogPane(DigitalScroll selectedScroll) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = sdf.format((selectedScroll.getCreate_time()));
        String updateTime = sdf.format((selectedScroll.getUpdate_time()));

        this.scrollview_textField_dialogID.setText(String.valueOf(selectedScroll.getScroll_id()));
        this.scrollview_textField_dialogName.setText(selectedScroll.getScroll_name());
        this.scrollview_textField_dialogFileName.setText(selectedScroll.getFile_name());
        this.scrollview_textField_dialogUploader.setText(String.valueOf(selectedScroll.getUploader_id()));
        this.scrollview_textField_dialogUploaderName.setText(String.valueOf(selectedScroll.getUploader_name()));
        this.scrollview_textField_dialogCreateTime.setText(createTime);
        this.scrollview_textField_dialogUpdateTime.setText(updateTime);
        this.scrollview_textField_dialogUploadTimes.setText(String.valueOf(selectedScroll.getUpload_times()));
        this.scrollview_textField_dialogDownloadTimes.setText(String.valueOf(selectedScroll.getDownload_times()));
        this.scrollview_textField_dialogFileSize.setText(String.format("%.2f KB", selectedScroll.getFile_size() / 1024.0));
        this.scrollview_textField_dialogDescription.setText(selectedScroll.getScroll_description());

        // open dialog pane
        this.scrollview_anchorpane_dialogPaneRoot.setVisible(true);

        // change the button visible
        switch (LocalStorage.getInstance().getUser_type()){
            case DEFINE.USER_TYPE_GUEST -> {;
                this.scrollview_button_dialogDownload.setVisible(false);
                this.scrollview_button_dialogCancel.setVisible(true);
            }
            case DEFINE.USER_TYPE_REGULAR -> {
                this.scrollview_button_dialogDownload.setVisible(true);
                this.scrollview_button_dialogCancel.setVisible(true);
            }
            case DEFINE.USER_TYPE_ADMIN ->{
                this.scrollview_button_dialogDownload.setVisible(true);
                this.scrollview_button_dialogCancel.setVisible(true);
            }
            default -> throw new RuntimeException("Do not exist the user type");
        }
    }

    private void closeDialogPane(){
        // close dialog pane
        this.scrollview_anchorpane_dialogPaneRoot.setVisible(false);
    }


    private void getAllScrollData() {

        // create a task
        Task<Message> getScrolls = new Task<>() {
            @Override
            protected Message call() {

                // get data from database
                List<Pojo> pojoList = digitalScrollDAO.selectAll();

                digitalScrollDAO.restartConnection();

                // convert the Pojo list to the DigitalScroll list
                List<DigitalScroll> digitalScrollList = new ArrayList<>();
                for (Pojo item : pojoList) {
                    if (item instanceof DigitalScroll) {
                        digitalScrollList.add((DigitalScroll) item);
                    }
                }

                // store the data to the local
                LocalStorage.getInstance().setScrollList(digitalScrollList);

                // print the result
                if (LocalStorage.getInstance().getScrollList().isEmpty()) {
                    return new Message(Message.Status.FAIL, "Data get failed, please try again");
                } else {
                    return new Message(Message.Status.SUCCESS, "Data get succeed");
                }
            }
        };

        getScrolls.setOnSucceeded(e -> {

            // set table items
            setTableItems(LocalStorage.getInstance().getScrollList());

            Message returnMsg = getScrolls.getValue();
            Toast.show(returnMsg, this.currentStage);
            enabledComponents();

            // set result text
            scrollview_label_searchResult.setText("Total "+ LocalStorage.getInstance().getScrollList().size() +" results");

            if (returnMsg.getStatus() != Message.Status.SUCCESS && returnMsg.getStatus() != Message.Status.FAIL) {
                // Invalid status!!!
                throw new RuntimeException("Invalid status!!!");
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

    private void setTableItems(List<DigitalScroll> scrolls) {

        scrollview_tableitem_id.setCellValueFactory(new PropertyValueFactory<>("scroll_id"));
        scrollview_tableitem_name.setCellValueFactory(new PropertyValueFactory<>("scroll_name"));
        scrollview_tableitem_downloadTimes.setCellValueFactory(new PropertyValueFactory<>("download_times"));
        scrollview_tableitem_uploaderName.setCellValueFactory(new PropertyValueFactory<>("uploader_name"));
        scrollview_tableitem_createTime.setCellValueFactory(new PropertyValueFactory<>("create_time"));
        scrollview_tableitem_createTime.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    setText(sdf.format(item));
                }
            }
        });
        ObservableList<DigitalScroll> data = FXCollections.observableArrayList(scrolls);
        scrollview_tableview_scrollList.setItems(data);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    private void enabledComponents() {
        this.scrollview_label_loadData.setVisible(false);
        this.scrollview_progressbar_loadData.setVisible(false);
        this.scrollview_tableview_scrollList.setDisable(false);
        this.scrollview_button_search.setDisable(false);
        this.scrollview_button_refresh.setDisable(false);
        this.scrollview_textfield_searchBox.setDisable(false);

        // set parent scene button components
        if (this.parentsSceneController != null){
            UserHomeController controller = (UserHomeController)this.parentsSceneController;
            controller.enabledAllUserHomeButtons();
        }

    }

    private void disabledComponents() {
        this.scrollview_label_loadData.setVisible(true);
        this.scrollview_progressbar_loadData.setVisible(true);
        this.scrollview_tableview_scrollList.setDisable(true);
        this.scrollview_button_search.setDisable(true);
        this.scrollview_button_refresh.setDisable(true);
        this.scrollview_textfield_searchBox.setDisable(true);

        // set parent scene button components
        if (this.parentsSceneController != null){
            UserHomeController controller = (UserHomeController)this.parentsSceneController;
            controller.disabledAllUserHomeButtons();
        }

    }

    private void disabledAllDialogTxtField(){
        if (!Objects.equals(LocalStorage.getInstance().getUser_type(), DEFINE.USER_TYPE_ADMIN)){
            this.scrollview_textField_dialogName.setDisable(true);
            this.scrollview_textField_dialogDescription.setDisable(true);
        }
    }

    private void downloadEnableComponents(){
        enabledComponents();
        this.scrollview_dialogpane_scrollDetail.setDisable(false);
        this.scrollview_pane_overlay.setDisable(false);
    }

    private void downloadDisableComponents(){
        disabledComponents();
        this.scrollview_dialogpane_scrollDetail.setDisable(true);
        this.scrollview_pane_overlay.setDisable(true);
    }

    @FXML
    private Button prevew_button_dialogsave;

    @FXML
    private Button preview_button_dialogback;

    @FXML
    private DialogPane preview_dialogpane_scrollID;

    @FXML
    private AnchorPane preview_pane_dialogmain;

    @FXML
    private Pane preview_pane_overlay;
    @FXML
    private AnchorPane preview_anchorpane_dialogpaneroot;

    @FXML
    private TextArea prewview_textarea_showtext;
    private void enable_preview_component(){
        preview_anchorpane_dialogpaneroot.setVisible(true);
        preview_pane_overlay.setVisible(true);
        if(!LocalStorage.getInstance().getUser_type().equalsIgnoreCase("guest")){
            prevew_button_dialogsave.setVisible(false);
        }
        preview_dialogpane_scrollID.setVisible(true);
        preview_pane_dialogmain.setVisible(true);
        preview_button_dialogback.setVisible(true);

    }
    @FXML
    void OnDialogPreviewBack(ActionEvent event) {
        preview_anchorpane_dialogpaneroot.setVisible(false);
        preview_pane_overlay.setVisible(false);
        preview_dialogpane_scrollID.setVisible(false);
        preview_pane_dialogmain.setVisible(false);
        preview_button_dialogback.setVisible(false);
        prewview_textarea_showtext.setVisible(false);
    }

    @FXML
    void OnDialogPreviewSave(ActionEvent event) {
        if(scrollClicked != null) {
            String newContent = prewview_textarea_showtext.getText();
            byte[] bytes = newContent.getBytes(StandardCharsets.UTF_8);
            digitalScrollDAO.updateBlob(scrollClicked.getScroll_name(), bytes);
            Toast.show(new Message(Message.Status.SUCCESS, "Changes Saved"),currentStage);
        }

    }
}
