package com.sunlight.client.gui.fx.controllers;

import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class InProgressWindowController implements Initializable {
    private static Logger logger = LoggerFactory.getLogger(InProgressWindowController.class);

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("[InProgressWindowController.initialize] location : {}", location);
        this.bundle = resources;
    }
}
