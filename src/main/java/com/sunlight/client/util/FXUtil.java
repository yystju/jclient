package com.sunlight.client.util;

import javafx.scene.control.Alert;

public class FXUtil {
    public static void alert(String title, String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(text);
        a.setResizable(true);
        //a.setContentText(text);
        a.showAndWait();
    }
}
