package vsas.controllerTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit5.Start;
import vsas.config.HikariCPConfig;
import vsas.config.MybatisConfig;
import vsas.controller.JumpPage;
import vsas.controller.LoginController;
import vsas.controller.ScrollViewController;

import java.util.Objects;

public class ViewScrollControllerTest {
    @Start
    public void start(Stage stage) throws Exception {
        // load connection pool
        MybatisConfig.getInstance();
        HikariCPConfig.getInstance();

        // Load the FXML file and set the controller
        FXMLLoader loader = new FXMLLoader();
        loader.setController(new ScrollViewController());
        loader.setLocation(JumpPage.class.getResource("/layout/login.fxml"));
        Parent loginRoot = loader.load();

        // Set the values for username and password
        LoginController controller = loader.getController();

        // set scene
        Scene scene = new Scene(loginRoot);
        scene.getStylesheets().add(Objects.requireNonNull(JumpPage.class.getResource("/style/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.toFront();

    }
}
