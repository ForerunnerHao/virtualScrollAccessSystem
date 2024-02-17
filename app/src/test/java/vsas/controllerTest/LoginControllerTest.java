package vsas.controllerTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import vsas.config.HikariCPConfig;
import vsas.config.MybatisConfig;
import vsas.controller.JumpPage;
import vsas.controller.LoginController;
import vsas.localstorage.LocalStorage;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class LoginControllerTest {
    TextField usernameField;
    PasswordField passwordField;

    @Start
    public void start(Stage stage) throws Exception {
        // load connection pool
        MybatisConfig.getInstance();
        HikariCPConfig.getInstance();

        // Load the FXML file and set the controller
        FXMLLoader loader = new FXMLLoader();
        loader.setController(new LoginController());
        loader.setLocation(JumpPage.class.getResource("/layout/login.fxml"));
        Parent loginRoot = loader.load();

        // Set the values for username and password
        LoginController controller = loader.getController();
        usernameField = controller.getLogin_textField_username();
        usernameField.setText("test123 4");
        passwordField = controller.getLogin_passwordField_password();
        passwordField.setText("test1234");

        // set scene
        Scene scene = new Scene(loginRoot);
        scene.getStylesheets().add(Objects.requireNonNull(JumpPage.class.getResource("/style/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.toFront();

    }

    @Test
    public void testLogin(FxRobot robot) {
//        // Click on the login button
//        robot.clickOn("#login_button_login");
//
//        // wait for 2.5 second
//        WaitForAsyncUtils.sleep(2500, TimeUnit.MILLISECONDS);
//
//        // null / empty
//        usernameField.setText("1");
//        passwordField.setText("test1234");
//
//        // Click on the login button
//        robot.clickOn("#login_button_login");
//        WaitForAsyncUtils.sleep(2500, TimeUnit.MILLISECONDS);
//
//        // empty
//        usernameField.setText("");
//        passwordField.setText("");
//
//        // Click on the login button
//        robot.clickOn("#login_button_login");
//        WaitForAsyncUtils.sleep(2500, TimeUnit.MILLISECONDS);
//
//        // null
//        usernameField.setText(null);
//        passwordField.setText(null);
//
//        // Click on the login button
//        robot.clickOn("#login_button_login");
//        WaitForAsyncUtils.sleep(2500, TimeUnit.MILLISECONDS);
//
//        // invalid username and password
//        usernameField.setText("test12341");
//        passwordField.setText("test1234");
//
//        // Click on the login button
//        robot.clickOn("#login_button_login");
//        WaitForAsyncUtils.sleep(3000, TimeUnit.MILLISECONDS);

        // correct username and password
        usernameField.setText("superAdmin");
        passwordField.setText("superAdmin");

        // Click on the login button
        robot.clickOn("#login_button_login");
        WaitForAsyncUtils.sleep(3000, TimeUnit.MILLISECONDS);

        LocalStorage.getInstance().setTestMode(true);
        WaitForAsyncUtils.sleep(500, TimeUnit.MILLISECONDS);

        // Click on the view scroll button
        robot.clickOn("#userHome_button_viewAllScrolls");
        WaitForAsyncUtils.sleep(4000, TimeUnit.MILLISECONDS);

        // Click on the view scroll search button
        // Click on the TextField to give it focus
        robot.clickOn("#scrollview_textfield_searchBox");
        WaitForAsyncUtils.sleep(1000, TimeUnit.MILLISECONDS);
        robot.write("a");
        WaitForAsyncUtils.sleep(1000, TimeUnit.MILLISECONDS);
        robot.clickOn("#scrollview_button_search");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);
        robot.clickOn("#scrollview_textfield_searchBox");
        WaitForAsyncUtils.sleep(1000, TimeUnit.MILLISECONDS);
        robot.press(KeyCode.BACK_SPACE);
        robot.release(KeyCode.BACK_SPACE);
        WaitForAsyncUtils.sleep(1000, TimeUnit.MILLISECONDS);
        robot.clickOn("#scrollview_button_search");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

        // Click on the first row of the TableView
        robot.clickOn(".table-row-cell");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

        // Click on download
        robot.clickOn("#scrollview_button_dialogDownload");
        WaitForAsyncUtils.sleep(6000, TimeUnit.MILLISECONDS);
        // download again
        robot.clickOn("#scrollview_button_dialogDownload");
        // wait Alert
        WaitForAsyncUtils.waitForFxEvents();
        // click OK
        robot.clickOn("Continue");
        WaitForAsyncUtils.sleep(6000, TimeUnit.MILLISECONDS);

        // back main page
        robot.clickOn("#scrollview_button_dialogCancel");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

        // uploadMyScroll
        robot.clickOn("#userHome_button_uploadMyScroll");
        WaitForAsyncUtils.sleep(4000, TimeUnit.MILLISECONDS);

        // viewMyAccount
        robot.clickOn("#userHome_button_viewMyAccount");
        WaitForAsyncUtils.sleep(4000, TimeUnit.MILLISECONDS);

        // viewUploadHistory
        robot.clickOn("#userHome_button_viewUploadHistory");
        WaitForAsyncUtils.sleep(4000, TimeUnit.MILLISECONDS);

        // createAccount
        robot.clickOn("#userHome_button_createAccount");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

        // Click on the logout button
        robot.clickOn("#userHome_button_logout");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

        // Click on the guest button
        robot.clickOn("#login_text_guestAccess");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

        // Click on the logout button
        robot.clickOn("#userHome_button_logout");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

        // Click on the signup button
        robot.clickOn("#login_button_signup");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

        // Click on the back login page button
        robot.clickOn("#signup_button_backLogin");
        WaitForAsyncUtils.sleep(2000, TimeUnit.MILLISECONDS);

    }
}
