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
import javafx.stage.Stage;
import vsas.component.Toast;
import vsas.dao.UserDownloadDAO;
import vsas.localstorage.LocalStorage;
import vsas.pojo.DigitalScroll;
import vsas.pojo.Message;
import vsas.pojo.Pojo;
import vsas.pojo.UserDownload;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DownloadHistoryController implements SceneController {

    private final UserDownloadDAO userDownloadDAO = UserDownloadDAO.getInstance();
    private SceneController parentsSceneController;
    private Stage currentStage;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane downloadView_anchorpane_dialogPaneRoot;

    @FXML
    private AnchorPane downloadView_anchorpane_waitingPaneRoot;

    @FXML
    private Button downloadView_button_dialogCancel;

    @FXML
    private Button downloadView_button_dialogDelete;

    @FXML
    private Button downloadView_button_refresh;

    @FXML
    private Button downloadView_button_search;

    @FXML
    private DialogPane downloadView_dialogpane_scrollDetail;

    @FXML
    private GridPane downloadView_grippane_mainTable;

    @FXML
    private Label downloadView_label_downloadLabel;

    @FXML
    private Label downloadView_label_loadData;

    @FXML
    private Label downloadView_textField_userDownloadId;

    @FXML
    private Label downloadView_label_totalSearchnum;

    @FXML
    private Label downloadView_label_fileName;

    @FXML
    private AnchorPane downloadView_pane_dialogMain;

    @FXML
    private Pane downloadView_pane_overlay;

    @FXML
    private ProgressBar downloadView_progressBar_downloadProgress;

    @FXML
    private ProgressBar downloadView_progressbar_loadData;

    @FXML
    private TableColumn<UserDownload, Integer> downloadView_tableitem_id;

    @FXML
    private TableColumn<UserDownload, String> downloadView_tableitem_name;

    @FXML
    private TableColumn<UserDownload, String> downloadView_tableitem_uploaderName;

    @FXML
    private TableColumn<UserDownload, Integer> downloadView_tableitem_no;

    @FXML
    private TableColumn<UserDownload, Long> downloadView_tableitem_size;

    @FXML
    private TableColumn<UserDownload, Timestamp> downloadView_tableitem_downloadTime;

    @FXML
    private TableView<UserDownload> downloadView_tableview_downloadHistory;

    @FXML
    private Label downloadView_textField_dialogDownloadTime;

    @FXML
    private Label downloadView_textField_dialogFileSize;

    @FXML
    private Label downloadView_textField_dialogID;

    @FXML
    private Label downloadView_textField_dialogName;

    @FXML
    private Label downloadView_textField_dialogUploaderName;

    @FXML
    private Label downloadView_textField_dialogUploaderId;

    @FXML
    private Text downloadView_text_title;

    @FXML
    private TextField downloadView_textfield_searchBox;

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
                UserDownload selectedScroll = (UserDownload) row.getItem();

                int selectedId = selectedScroll.getDownloadId();
                System.out.println("Selected DownloadId: " + selectedId);

                // Open the sub-pane here
                openSubScene(selectedScroll);
            }
        }
    }

    @FXML
    void onDialogCancelAction(ActionEvent event) {
        this.downloadView_anchorpane_dialogPaneRoot.setVisible(false);
    }

    @FXML
    void onDialogDeleteAction(ActionEvent event) {
        disabledComponents();
        this.downloadView_anchorpane_dialogPaneRoot.setVisible(false);
        deleteUserDownloadHistory(Integer.parseInt(downloadView_textField_userDownloadId.getText()));
    }

    @FXML
    void onDialogOverlayAction(MouseEvent event) {

    }

    @FXML
    void onRefreshAction(ActionEvent event) {
        disabledComponents();
        getAllScrollData();
    }

    @FXML
    void onDownloadScrollSearchAction(ActionEvent event) {
        disabledComponents();
        String searchContent = this.downloadView_textfield_searchBox.getText().trim().toLowerCase();
        List<UserDownload> searchScrolls = new ArrayList<>();
        List<UserDownload> localScrolls = LocalStorage.getInstance().getDownloadedScrollList();

        if (searchContent.isEmpty()) {
            this.downloadView_label_totalSearchnum.setText("Total " + localScrolls.size() + " results");

            // set table view's items
            setTableItems(localScrolls);

        } else {
            // find relative scrolls
            for (UserDownload ud : localScrolls) {
                if (ud.getScroll_name().toLowerCase().contains(searchContent)) {
                    searchScrolls.add(ud);
                }
            }
            this.downloadView_label_totalSearchnum.setText("Total " + searchScrolls.size() + " results");

            // set table view's items
            setTableItems(searchScrolls);
        }
        enabledComponents();
    }

    @FXML
    void initialize() {

        // setting tableView placeholder content
        this.downloadView_anchorpane_waitingPaneRoot.setVisible(false);
        this.downloadView_anchorpane_dialogPaneRoot.setVisible(false);

        Label placeholderLabel = new Label("The data in the table is empty");
        downloadView_tableview_downloadHistory.setPlaceholder(placeholderLabel);

        // get local data
        List<UserDownload> scrolls = LocalStorage.getInstance().getDownloadedScrollList();

        // if local data not exist
        if (scrolls.isEmpty()) {

            // loading data disabled components
            disabledComponents();

            // new thread to get data
            getAllScrollData();
        } else {
            Toast.show(new Message(Message.Status.SUCCESS, "Local cache load succeed"), this.currentStage);

            // load cache data
            setTableItems(scrolls);

            // set the result label
            downloadView_label_totalSearchnum.setText("Total " + LocalStorage.getInstance().getScrollList().size() + " results");

        }
    }

    private void openSubScene(UserDownload userDownload){

        this.downloadView_anchorpane_dialogPaneRoot.setVisible(true);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.downloadView_textField_dialogDownloadTime.setText(sdf.format(userDownload.getDownload_time()));
        this.downloadView_textField_dialogUploaderName.setText(userDownload.getUploader_name());
        this.downloadView_textField_dialogFileSize.setText(String.format("%.2f KB", userDownload.getFile_size() / 1024.0));
        this.downloadView_textField_dialogID.setText(String.valueOf(userDownload.getScroll_id()));
        this.downloadView_textField_dialogName.setText(userDownload.getScroll_name());
        this.downloadView_textField_dialogUploaderId.setText(String.valueOf(userDownload.getDownloaderId()));
        this.downloadView_textField_userDownloadId.setText(String.valueOf(userDownload.getDownloadId()));
        this.downloadView_label_fileName.setText(userDownload.getFile_name());
    }

    private void disabledComponents() {
        this.downloadView_tableview_downloadHistory.setDisable(true);
        this.downloadView_button_refresh.setDisable(true);
        this.downloadView_button_search.setDisable(true);
        this.downloadView_textfield_searchBox.setDisable(true);
        this.downloadView_anchorpane_waitingPaneRoot.setVisible(true);

        // set parent scene button components
        if (this.parentsSceneController != null) {
            UserHomeController controller = (UserHomeController) this.parentsSceneController;
            controller.disabledAllUserHomeButtons();
        }
    }

    private void enabledComponents() {
        this.downloadView_tableview_downloadHistory.setDisable(false);
        this.downloadView_button_refresh.setDisable(false);
        this.downloadView_button_search.setDisable(false);
        this.downloadView_textfield_searchBox.setDisable(false);
        this.downloadView_anchorpane_waitingPaneRoot.setVisible(false);

        // set parent scene button components
        if (this.parentsSceneController != null) {
            UserHomeController controller = (UserHomeController) this.parentsSceneController;
            controller.enabledAllUserHomeButtons();
        }

    }

    private void getAllScrollData() {

        // create a task
        Task<Message> getScrolls = new Task<>() {
            @Override
            protected Message call() {

                // get data from database
                List<UserDownload> userDownloadList = userDownloadDAO.selectDownloadDetailsByDownloaderId(LocalStorage.getInstance().getUser_id());

                userDownloadDAO.restartConnection();

                if (userDownloadList == null || userDownloadList.isEmpty()) return new Message(Message.Status.FAIL, "Cannot get data");

                // store the data to the local
                LocalStorage.getInstance().setDownloadedScrollList(userDownloadList);
                List<UserDownload> scrolls = LocalStorage.getInstance().getDownloadedScrollList();

                // print the result
                if (scrolls.isEmpty()) {
                    return new Message(Message.Status.FAIL, "Data get failed, please try again");
                } else {
                    return new Message(Message.Status.SUCCESS, "Data get succeed");
                }
            }
        };

        getScrolls.setOnSucceeded(e -> {

            Message returnMsg = getScrolls.getValue();
            Toast.show(returnMsg, this.currentStage);
            enabledComponents();

            // set table items
            setTableItems(LocalStorage.getInstance().getDownloadedScrollList());

            // set result text
            downloadView_label_totalSearchnum.setText("Total " + LocalStorage.getInstance().getDownloadedScrollList().size() + " results");

            if (returnMsg.getStatus() != Message.Status.SUCCESS && returnMsg.getStatus() != Message.Status.FAIL) {
                // Invalid status!!!
                throw new RuntimeException("Invalid status!!!");
            }
        });

        getScrolls.setOnFailed(e -> {
            Exception exception = (Exception) e.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), this.currentStage);
            enabledComponents();
        });

        new Thread(getScrolls).start();
    }

    private void deleteUserDownloadHistory(int userDownload_id){
        Task<Message> deleteHistory = new Task<>() {
            @Override
            protected Message call() {

                // get data from database
                boolean result = userDownloadDAO.delete(userDownload_id);
                userDownloadDAO.restartConnection();

                if (result){
                    return new Message(Message.Status.SUCCESS, "delete succeed");
                }else {
                    return new Message(Message.Status.FAIL, "delete failed");
                }
            }
        };

        deleteHistory.setOnSucceeded(e -> {
            Message returnMsg = deleteHistory.getValue();
            Toast.show(returnMsg, this.currentStage);
            enabledComponents();
            if (deleteHistory.getValue().getStatus() == Message.Status.SUCCESS){
                getAllScrollData();
            }
        });

        deleteHistory.setOnFailed(e -> {
            Exception exception = (Exception) e.getSource().getException();
            exception.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, exception.getMessage()), this.currentStage);
            enabledComponents();
        });

        new Thread(deleteHistory).start();
    }

    private void setTableItems(List<UserDownload> scrolls) {
        // set item no
        downloadView_tableitem_no.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    // Display the row number starting from 1
                    setText(Integer.toString(getIndex() + 1));
                }
            }
        });

        downloadView_tableitem_id.setCellValueFactory(new PropertyValueFactory<>("scroll_id"));
        downloadView_tableitem_name.setCellValueFactory(new PropertyValueFactory<>("scroll_name"));
        downloadView_tableitem_size.setCellValueFactory(new PropertyValueFactory<>("file_size"));
        downloadView_tableitem_uploaderName.setCellValueFactory(new PropertyValueFactory<>("uploader_name"));
        downloadView_tableitem_downloadTime.setCellValueFactory(new PropertyValueFactory<>("download_time"));
        downloadView_tableitem_downloadTime.setCellFactory(column -> new TableCell<>() {
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
        downloadView_tableitem_size.setCellFactory(column -> new TableCell<>() {
            @Override
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


        ObservableList<UserDownload> data = FXCollections.observableArrayList(scrolls);
        downloadView_tableview_downloadHistory.setItems(data);

    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    @Override
    public void setParentsSceneController(SceneController parentsSceneController) {
        this.parentsSceneController = parentsSceneController;
    }
}
