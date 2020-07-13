package sample.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.model.DataSource;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private ImageView progressBar;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button login;

    @FXML
    private CheckBox rememberMe;

    @FXML
    private static LoginController instance = new LoginController();

    private final static File rememberFile = new File("remember.txt");

    public LoginController() {
        instance = this;
    }

    @FXML
    public static LoginController getInstance() {
        return instance;
    }

    public static File getRememberFile() {
        return rememberFile;
    }

    @FXML
    public CheckBox getRememberMe() {
        return rememberMe;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setVisible(false);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(rememberFile))) {
            if (rememberFile.length() != 0) {
                rememberMe.setSelected(true);
                username.setText(bufferedReader.readLine());
                password.setText(bufferedReader.readLine());
            } else {
                rememberMe.setSelected(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void login() {
        progressBar.setVisible(true);
        PauseTransition pt = new PauseTransition(Duration.seconds(2));
        pt.setOnFinished(event -> {
            try {
                if (DataSource.getInstance().loginQuery(username.getText(), password.getText())) {
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(rememberFile))) {
                        if (rememberMe.isSelected()) {
                            bufferedWriter.write(username.getText());
                            bufferedWriter.newLine();
                            bufferedWriter.write(password.getText());
                        } else {
                            bufferedWriter.flush();
                            rememberMe.setSelected(false);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                    Stage home = new Stage();
                    home.getIcons().add(new Image("/sample/password_icon.png"));
                    home.setTitle("Password VAULT");
                    home.setResizable(false);
                    login.getScene().getWindow().hide();
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/sample/fxml/MainWindow.fxml"));
                        Scene scene = new Scene(root);
                        home.setScene(scene);
                        home.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    username.clear();
                    password.clear();
                    progressBar.setVisible(false);
                    return;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        });

        pt.play();
    }

    @FXML
    public void signUp() {
        login.getScene().getWindow().hide();
        try {
            Stage signUp = new Stage();
            signUp.getIcons().add(new Image("/sample/password_icon.png"));
            signUp.setTitle("Password VAULT");
            Parent root = FXMLLoader.load(getClass().getResource("/sample/fxml/SignUp.fxml"));
            Scene scene = new Scene(root);
            signUp.setScene(scene);
            signUp.show();
            signUp.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
            progressBar.setVisible(false);
        }
    }


    @FXML
    public String getUsername() {
        return username.getText();
    }

    @FXML
    public String getPassword() {
        return password.getText();
    }

}
