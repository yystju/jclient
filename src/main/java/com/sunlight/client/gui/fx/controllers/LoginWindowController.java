package com.sunlight.client.gui.fx.controllers;

import com.sunlight.client.ClientApplication;
import com.sunlight.client.api.TaharaService;
import com.sunlight.client.gui.fx.vo.ClientConfiguration;
import com.sunlight.client.util.ClientConfigurationUtil;
import com.sunlight.client.util.FXUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginWindowController implements Initializable {
    private static Logger logger = LoggerFactory.getLogger(LoginWindowController.class);

    @FXML
    TextField usernameField;

    @FXML
    PasswordField passwordField;

    @FXML
    TextField equipmentNameField;

    private TaharaService taharaService;
    private ResourceBundle bundle;

    private ClientConfiguration clientConfiguration;

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        logger.info("[LoginWindowController.initialize] location : {}", location);

        this.bundle = bundle;

        ApplicationContext context = ClientApplication.getContext();

        this.taharaService = context.getBean(TaharaService.class);

        logger.info("taharaService : {}", this.taharaService);

        ClientConfigurationUtil.loadClientConfiguration();

        this.clientConfiguration = ClientConfigurationUtil.getClientConfiguration();

        if(clientConfiguration != null) {
            this.equipmentNameField.setText(clientConfiguration.getEquipmentName());
            this.usernameField.setText(clientConfiguration.getUsername());
        }

        this.passwordField.requestFocus();
    }

    public void handleKeyPress(KeyEvent event) {
        logger.info("[LoginWindowController.handleKeyPress]");

        if(event.getCode() == KeyCode.ENTER) {
            handleSubmit(new ActionEvent(event.getSource(), event.getTarget()));
        }
    }

    public void handleSubmit(ActionEvent event) {
        logger.info("[LoginWindowController.handleSubmit]");

        String equipmentName = equipmentNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        logger.info("username : {}", username);
//        logger.info("password : {}", password);
        logger.info("password : ********");

        enableControls(false);

        if(StringUtils.isEmpty(equipmentName)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("equipmentEmpty"));

            enableControls(true);

            this.equipmentNameField.requestFocus();
            return;
        }

        if(StringUtils.isEmpty(username)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("usernameEmpty"));

            enableControls(true);

            this.usernameField.requestFocus();
            return;
        }

        if(StringUtils.isEmpty(password)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("passwordEmpty"));

            enableControls(true);

            this.passwordField.requestFocus();
            return;
        }

        if(taharaService.authenticate(username, password)) {
            this.taharaService.setEquipmentName(equipmentName);

            this.clientConfiguration.setEquipmentName(equipmentName);
            this.clientConfiguration.setUsername(username);
            ClientConfigurationUtil.saveClientConfiguration();

            startMainWindow(event);
        } else {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("ivalidAuthentication"));
            enableControls(true);
        }
    }

    public void handleCancel(ActionEvent event) {
        logger.info("[LoginWindowController.handleCancel]");
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    private void startMainWindow(ActionEvent event) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("fx.bundle.Main");
            Parent root = FXMLLoader.load(getClass().getResource("/fx/Main.fxml"), bundle);

            Stage stage = new Stage();
            stage.setTitle(bundle.getString("title"));
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.show();

            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void enableControls(boolean b) {
        usernameField.setEditable(b);
        passwordField.setEditable(b);
        equipmentNameField.setEditable(b);
    }
}
