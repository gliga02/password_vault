package sample.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.model.Account;
import sample.model.DataSource;
import sample.model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private AnchorPane holderPane;

    @FXML
    private AnchorPane myAccounts;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label countLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createPage();
        setWelcomeLabel(LoginController.getInstance().getUsername());
        setCountLabel(DataSource.getInstance().countAccountsForUser(LoginController.getInstance().getUsername(), LoginController.getInstance().getPassword()));
    }

    private void setNode(Node node) {
        holderPane.getChildren().clear();
        holderPane.getChildren().add(node);
    }

    public void setWelcomeLabel(String name) {
        welcomeLabel.setText(name);
    }

    public void setCountLabel(int number) {
        countLabel.setText("" + number);
    }


    public void deleteAccountEvent() {
        if (!countLabel.getText().equals("0")) {
            if (MyAccountsController.getInstance().getTableView().getSelectionModel().getSelectedItem() != null) {
                int numOfAccounts = Integer.parseInt(countLabel.getText());
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(holderPane.getScene().getWindow());
                dialog.setTitle("Deleting account");
                dialog.setHeaderText(null);
                dialog.setContentText("Are you sure you want to delete this account?");
                dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    MyAccountsController.getInstance().deleteAccount();
                    numOfAccounts--;
                    countLabel.setText(String.valueOf(numOfAccounts));
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Account isn't selected");
                alert.setHeaderText(null);
                alert.setContentText("You have to select account which you want to delete");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You have no accounts");
            alert.show();
        }
    }

    public void createPage() {
        try {
            myAccounts = FXMLLoader.load(getClass().getResource("/sample/fxml/MyAccounts.fxml"));
            setNode(myAccounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        Platform.exit();
    }

    public void logout() {
        welcomeLabel.getScene().getWindow().hide();
        Stage login = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/sample/fxml/LoginMain.fxml"));
            Scene scene = new Scene(root);
            login.setScene(scene);
            login.getIcons().add(new Image("/sample/password_icon.png"));
            login.setTitle("Password VAULT");
            login.show();
            login.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteUserAccount() {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(holderPane.getScene().getWindow());
            dialog.setTitle("Deleting your account");
            dialog.setHeaderText(null);
            StringBuilder sb = new StringBuilder("Are you sure you want to delete this account?\n");
            sb.append("WARNING: If you delete your account, all data you saved will be lost");
            dialog.setContentText(sb.toString());
            dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                DataSource.getInstance().deleteUserAccount(LoginController.getInstance().getUsername(), LoginController.getInstance().getPassword());
                LoginController.getInstance().getRememberMe().setSelected(false);
                LoginController.getRememberFile().delete();
                try {
                    LoginController.getRememberFile().createNewFile();
                    welcomeLabel.getScene().getWindow().hide();
                    Stage login = new Stage();
                    Parent root;
                    try {
                        root = FXMLLoader.load(getClass().getResource("/sample/fxml/LoginMain.fxml"));
                        Scene scene = new Scene(root);
                        login.setScene(scene);
                        login.show();
                        login.setResizable(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void addAccount() {
        int numOfAccounts = Integer.parseInt(countLabel.getText());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(holderPane.getScene().getWindow());
        dialog.setTitle("Add account");
        dialog.setHeaderText("Add new account: ");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/sample/fxml/addDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            numOfAccounts++;
            AddDialogController addController = fxmlLoader.getController();
            Account account = addController.newAccount();
            User user = new User();
            user.setName(DataSource.getInstance().queryName(LoginController.getInstance().getUsername(), LoginController.getInstance().getPassword()));
            user.setUsername(LoginController.getInstance().getUsername());
            user.setPassword(LoginController.getInstance().getPassword());
            DataSource.getInstance().insertAccounts(user,
                    account.getService(), account.getEmail(), account.getUsername(), account.getPassword());
            Task<ObservableList<Account>> task = new GetAccountsForUser();
            MyAccountsController.getInstance().getTableView().itemsProperty().bind(task.valueProperty());
            new Thread(task).start();
            countLabel.setText(String.valueOf(numOfAccounts));
        }
    }
}


