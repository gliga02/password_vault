module Password.Vault {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens sample.controller;
    opens sample.fxml;
    opens sample.model;
    opens sample;
}