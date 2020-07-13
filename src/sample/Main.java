package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.model.DataSource;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxml/LoginMain.fxml"));
        primaryStage.setTitle("Password VAULT");
        primaryStage.getIcons().add(new Image("/sample/password_icon.png"));
        primaryStage.setScene(new Scene(root, 580, 370));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    @Override
    public void init() throws Exception {
        if (!DataSource.getInstance().open()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("FATAL ERROR: Couldn't connect to db");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.setAlwaysOnTop(true);
                stage.toFront();
                stage.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    Platform.exit();
                }
            });
        }
    }

    @Override
    public void stop() throws Exception {
        DataSource.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
