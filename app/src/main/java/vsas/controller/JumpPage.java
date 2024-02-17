package vsas.controller;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vsas.component.Toast;
import vsas.pojo.Message;
import vsas.utils.DEFINE;
import java.io.IOException;
import java.util.Objects;

import static vsas.utils.DEFINE.*;

public class JumpPage {
    private static Scene getScene(String jumpPageName) throws IOException {

        // create a FXML file loader
        FXMLLoader loader = new FXMLLoader();

        // bind corresponding controller class
        switch (jumpPageName){
            case PAGE_NAME_USER_HOME -> loader.setController(new UserHomeController());
            case PAGE_NAME_LOGIN -> loader.setController(new LoginController());
            case PAGE_NAME_SIGNUP -> loader.setController(new SignUpController());

//            case PAGE_NAME_SCROLL_VIEW -> loader.setController(new LoginController());
            default -> throw new RuntimeException("Do not exist the page");
        }

        // load corresponding fxml file
        loader.setLocation(JumpPage.class.getResource("/layout/" + jumpPageName + ".fxml"));

        // load loader into the base class for node container classes in JavaFX scene graphs
        Parent loginRoot = loader.load();

        // create the scene
        Scene scene = new Scene(loginRoot);

        // bind css class to the scene
        scene.getStylesheets().add(Objects.requireNonNull(JumpPage.class.getResource("/style/style.css")).toExternalForm());

        return scene;
    }

    public static Scene getLoginScene() throws IOException {
       return getScene(PAGE_NAME_LOGIN);
    }

    public static void jumpToOtherPage(Event event, String pageName) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try{
            // set new Scene to Stage
            currentStage.setScene(JumpPage.getScene(pageName));

            //change window's size
            DEFINE.setWindowSize(currentStage, pageName);

            // show target page
            currentStage.show();

        }catch (IOException | RuntimeException e){
            e.printStackTrace();
            Toast.show(new Message(Message.Status.FAIL, "Check the controller is set correctly"), currentStage);
        }
    }
}
