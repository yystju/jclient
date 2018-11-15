package com.sunlight.client.gui.fx.controllers;

import com.sunlight.client.ClientApplication;
import com.sunlight.client.api.TaharaService;
import com.sunlight.client.gui.fx.vo.ClientConfiguration;
import com.sunlight.client.util.FXUtil;
import com.sunlight.client.vo.*;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
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
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

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

    private TaharaService taharaService;

    private ClientConfiguration clientConfiguration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("[FailureWindowController.initialize] location : {}", location);

        this.bundle = resources;

        ApplicationContext context = ClientApplication.getContext();

        this.taharaService = context.getBean(TaharaService.class);

        logger.info("taharaService : {}", this.taharaService);

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
                    do10010(clientConfiguration.getPackingSN(), wipno);
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

    private void do10010(String sn, String wipno) {
        if(StringUtils.isEmpty(sn)) {
            return;
        }

        if(StringUtils.isEmpty(wipno)) {
            return;
        }

        final String equipmentName = this.clientConfiguration.getEquipmentName();

        logger.info("[MainWindowController.do10010] sn : {}, wipno : {}, equipmentName : {}", sn, wipno, equipmentName);


        Observable.create((ObservableOnSubscribe<Message>) (emitter) -> {
            String transactionId = String.format("%s-%d", equipmentName, System.currentTimeMillis());

            Message message = new Message();

            message.setBody(new Body());
            message.setHeader(new Header());
            message.getHeader().setLocation(new Location());
            message.getBody().setProduct(new Product());
            message.getBody().setPackageContainer(new PackageContainer());

            message.getHeader().setMessageClass("10010");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(transactionId);
            message.getHeader().getLocation().setRouteName(equipmentName);
            message.getHeader().getLocation().setEquipmentName(equipmentName);
            message.getHeader().getLocation().setZoneName(equipmentName);

            message.getBody().getPackageContainer().setNumber(sn);
            message.getBody().getPackageContainer().setType("0"); // 0 - ³öÏä£¬ 1 - ÈëÏä¡£
            message.getBody().getProduct().setNumber(wipno);

            emitter.onNext(message);
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.io())
        .subscribe(request -> {
            try {
                taharaService.process10010(request, (Message result) -> {
                    logger.info("{} :: {}", request.getHeader().getTransactionID(), result.getHeader().getTransactionID());

                    if(!request.getHeader().getTransactionID().equals(result.getHeader().getTransactionID())) {
                        return;
                    }

                    Platform.runLater(() -> {
                        if(!"0".equals(result.getBody().getResult().getErrorCode())) {
                            FXUtil.error(this.bundle.getString("alert.error.title"), String.format(this.bundle.getString("alert.error.occurred"), result.getBody().getResult().getErrorCode(), result.getBody().getResult().getErrorText()));
                            return;
                        }

                        this.mainPane.getScene().getWindow().hide();
                    });
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                Platform.runLater(() -> {
                    FXUtil.alert(bundle.getString("alert.error.title"), String.format(bundle.getString("alert.error.occurred"), ex.getMessage()));
                });
            }
        });
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

    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    public void setClientConfiguration(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }
}
