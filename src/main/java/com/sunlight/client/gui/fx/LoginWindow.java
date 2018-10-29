package com.sunlight.client.gui.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class LoginWindow extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("fx.bundle.Login");
            Parent root = FXMLLoader.load(getClass().getResource("/fx/Login.fxml"), bundle);
            primaryStage.setTitle(bundle.getString("title"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
