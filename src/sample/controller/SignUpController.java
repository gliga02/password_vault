package sample.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.model.DataSource;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    private ImageView progressBar;

    @FXML
    private Button signUp;

    @FXML
    private TextField name;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setVisible(false);
    }

    @FXML
    public void signUp() {
        progressBar.setVisible(true);
        PauseTransition pt = new PauseTransition();
        pt.setDuration(Duration.seconds(2));
        pt.setOnFinished(event -> {


            try {
                if (name.getText().isEmpty() || username.getText().isEmpty() || password.getText().isEmpty()) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Fields cannot be empty");
                        alert.showAndWait();
                        progressBar.setVisible(false);
                    });
                } else if (!username.getText().matches("(?=\\S+$).{8,}")) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Username must be at least 8 characters long and cannot contain blank spaces");
                        alert.showAndWait();
                        progressBar.setVisible(false);
                    });
                } else if (!password.getText().matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}")) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Password must contain at least one digit, lower case and upper case latter and must be at least 8 characters long");
                        alert.showAndWait();
                        progressBar.setVisible(false);
                    });

                } else {
                    if (!DataSource.getInstance().queryUsernames().contains(username.getText())) {
                        DataSource.getInstance().insertUsers(name.getText(), username.getText(), password.getText());
                        Stage home = new Stage();
                        home.getIcons().add(new Image("/sample/password_icon.png"));
                        home.setTitle("Password VAULT");
                        signUp.getScene().getWindow().hide();
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("/sample/fxml/LoginMain.fxml"));
                            Scene scene = new Scene(root);
                            home.setScene(scene);
                            Platform.runLater(() ->{
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setContentText("Signed up successfully");
                                alert.showAndWait();
                            });

                            home.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("This username already exists");
                            alert.showAndWait();
                        });

                        username.clear();
                        password.clear();
                        progressBar.setVisible(false);
                    }

                }

            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Sign up error");
                    alert.showAndWait();
                });
            }

        });

        pt.play();
        }

    @FXML
    public void login() {
        signUp.getScene().getWindow().hide();
        Stage login = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/sample/fxml/LoginMain.fxml"));
            Scene scene = new Scene(root);
            login.setScene(scene);
            login.show();
            login.getIcons().add(new Image("/sample/password_icon.png"));
            login.setTitle("Password VAULT");
            login.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
