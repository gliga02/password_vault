package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sample.model.Account;

public class AddDialogController {
    @FXML
    private TextField service;

    @FXML
    private TextField email;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    public Account newAccount() {
        String serviceAccount = service.getText();
        String emailAccount = email.getText();
        String usernameAccount = username.getText();
        String passwordAccount = password.getText();

        Account account = new Account();
        account.setService(serviceAccount);
        account.setUsername(usernameAccount);
        account.setPassword(passwordAccount);
        account.setEmail(emailAccount);

        return account;
    }
}
