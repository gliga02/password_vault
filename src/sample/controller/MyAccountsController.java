package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import sample.model.Account;
import sample.model.DataSource;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class MyAccountsController implements Initializable {

    public MyAccountsController() {
        instance = this;
    }

    private static MyAccountsController instance = new MyAccountsController();

    public static MyAccountsController getInstance() {
        return instance;
    }


    @FXML
    private TableView tableView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listAccountsForUser();
    }

    @FXML
    public TableView getTableView() {
        return tableView;
    }

    @FXML
    public void listAccountsForUser() {
        Task<ObservableList<Account>> task = new GetAccountsForUser();
        tableView.itemsProperty().bind(task.valueProperty());
        tableView.getSelectionModel().selectFirst();
        new Thread(task).start();
    }

    @FXML
    public void deleteAccount() {
        final Account account = (Account) tableView.getSelectionModel().getSelectedItem();
        if (account == null) {
            return;
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return DataSource.getInstance().deleteAccount(account.getId());
            }
        };

        task.setOnSucceeded(e -> {
            if (task.valueProperty().get()) {
                    tableView.getItems().remove(account);
                    tableView.refresh();
                    tableView.getSelectionModel().selectFirst();
            }
        });

        task.setOnFailed(e -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error while deleting account");
            alert.show();
        });

        new Thread(task).start();

    }
}

class GetAccountsForUser extends Task {
    @Override
    public ObservableList<Account> call() {
        try {
            return FXCollections.observableArrayList(DataSource.getInstance().queryAccountsUser(DataSource.getInstance().queryUserId(LoginController.
                    getInstance().getUsername(), LoginController.getInstance().getPassword())));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}


