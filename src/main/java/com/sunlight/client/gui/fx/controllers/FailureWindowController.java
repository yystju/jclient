package com.sunlight.client.gui.fx.controllers;

import com.sunlight.client.util.FXUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class FailureWindowController implements Initializable {
    private static Logger logger = LoggerFactory.getLogger(FailureWindowController.class);

    public static final String CSS_CLASS_ERROR = "error";

    private ResourceBundle bundle;

    private String wipno;
    private String errorMessage;

    private String result;

    private String text = "";

    @FXML
    AnchorPane mainPane;

    @FXML
    Label wipnoLabel;

    @FXML
    Label errorMessageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("[FailureWindowController.initialize] location : {}", location);

        this.bundle = resources;

        Platform.runLater(() -> {
            logger.info("wipno : {}", wipno);

            wipnoLabel.setText(String.format(this.bundle.getString("wipno.info"), wipno));
            errorMessageLabel.setText(String.format(this.bundle.getString("wipno.error"), errorMessage));
        });
    }

    public void onMainPaneKeyPress(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            String line = this.text.trim();

            this.text = "";

            logger.info("line : {}", line);

            if(wipno.equals(line)) {
                if(this.mainPane.getStyleClass().contains(CSS_CLASS_ERROR)) {
                    this.mainPane.getStyleClass().remove(CSS_CLASS_ERROR);
                }

                boolean choice = FXUtil.choice(this.bundle.getString("alert.match.title"), String.format(this.bundle.getString("alert.match.info"), line));

                logger.info("choice : {}", choice);

                if(choice) {
                    //TODO: Do the fetch out request and then close the window.

                    ((Node)(event.getSource())).getScene().getWindow().hide();
                }
            } else if ("-".equals(line)) {
                ((Node)(event.getSource())).getScene().getWindow().hide();
            } else {
                if(!this.mainPane.getStyleClass().contains(CSS_CLASS_ERROR)) {
                    this.mainPane.getStyleClass().add(CSS_CLASS_ERROR);
                }
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            this.text += event.getText();
        }
    }

    public String getWipno() {
        return wipno;
    }

    public void setWipno(String wipno) {
        this.wipno = wipno;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResult() {
        return result;
    }
}
