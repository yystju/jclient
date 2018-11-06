package com.sunlight.client.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class FXUtil {
    public static void alert(String title, String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(text);
        a.setResizable(true);
        //a.setContentText(text);
        a.showAndWait();
    }

    public static void error(String title, String text) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(text);
        a.setResizable(true);
        //a.setContentText(text);
        a.showAndWait();
    }

    public static String input(String title, String text, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setContentText(text);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        }

        return null;
    }
}
