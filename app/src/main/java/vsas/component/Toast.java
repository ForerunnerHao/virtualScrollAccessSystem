package vsas.component;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import vsas.pojo.Message;

public class Toast {

    public static void show(Message message, Stage ownerStage) {
        if (ownerStage == null) throw new IllegalArgumentException("ownerStage cannot be null");
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        Label label = new Label(message.getMsg());
        label.setPrefWidth(300);
        label.setPrefHeight(20);
        label.setAlignment(Pos.CENTER);

        switch (message.getStatus()){
            case SUCCESS -> {
                label.setStyle("-fx-background-color: rgba(240, 249, 235, 1); -fx-text-fill: rgba(103, 194, 58, 1); -fx-padding: 10px;");
                label.setWrapText(true);
            }
            case FAIL -> {
                label.setStyle("-fx-background-color: rgba(254, 240, 240, 1); -fx-text-fill: rgba(245, 108, 108, 1); -fx-padding: 10px;");
                label.setWrapText(true);
            }
            case INFO -> {
                label.setStyle("-fx-background-color: rgba(237, 242, 252, 1); -fx-text-fill: rgba(144, 147, 153, 1); -fx-padding: 10px;");
                label.setWrapText(true);
            }
            case WARNING -> {
                label.setStyle("-fx-background-color: rgba(253, 246, 236, 1); -fx-text-fill: rgba(237, 174, 72, 1); -fx-padding: 10px;");
                label.setWrapText(true);
            }
            default -> {
                throw new RuntimeException("Do not exits this status!!!");
            }
        }

        Platform.runLater(() -> {
            toastStage.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - label.getWidth() / 2);
            toastStage.setY(ownerStage.getY() + 50);
        });

        ownerStage.xProperty().addListener((obs, oldVal, newVal) -> updateToastPosition(toastStage, ownerStage, label));
        ownerStage.yProperty().addListener((obs, oldVal, newVal) -> updateToastPosition(toastStage, ownerStage, label));

        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-radius: 5; -fx-background-color: rgba(0, 0, 0, 0.5);");
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        fadeIn.play();

        fadeIn.setOnFinished((e) -> new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                // Handle exception
                ex.printStackTrace();
            }
            fadeOut.play();
        }).start());

        fadeOut.setOnFinished((e) -> toastStage.close());

        updateToastPosition(toastStage, ownerStage, label);
        toastStage.show();

    }

    private static void updateToastPosition(Stage toastStage, Stage ownerStage, Label label) {
        toastStage.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - label.getWidth() / 2);
        toastStage.setY(ownerStage.getY() + 50);
//        System.out.printf("ownerStageX: %.4f \n ownerStage.getWidth() / 2: %.4f\nlabel.getWidth() / 2: %.4f\n", ownerStage.getX(), ownerStage.getWidth() / 2, label.getWidth() / 2);
    }

}

