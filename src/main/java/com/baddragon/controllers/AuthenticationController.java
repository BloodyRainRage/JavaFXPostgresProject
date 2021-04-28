package com.baddragon.controllers;

import com.baddragon.database.Authenticator;
import com.baddragon.manager.AppManager;
import com.baddragon.mock.MockAuthenticator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class AuthenticationController implements Initializable {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    public void login(ActionEvent event) {
//        Authenticator authenticator = new MockAuthenticator();
        Authenticator authenticator = new Authenticator();
        try {
            if (authenticator.loginIsCorrect(loginField.getText())) {
                if (authenticator.checkLogPassPair(loginField.getText(), passwordField.getText())) {
                    try {
                        Stage primaryStage = new Stage();
                        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
                        Scene scene = new Scene(root, 1200, 460);
                        primaryStage.setScene(scene);
                        primaryStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void empty() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppManager.getInstance().addController("AutenticationController", this);
    }
}
