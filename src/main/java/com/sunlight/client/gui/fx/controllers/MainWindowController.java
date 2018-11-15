package com.sunlight.client.gui.fx.controllers;

import com.sunlight.client.ClientApplication;
import com.sunlight.client.api.TaharaService;
import com.sunlight.client.gui.fx.vo.ClientConfiguration;
import com.sunlight.client.gui.fx.vo.PackingInfo;
import com.sunlight.client.util.ClientConfigurationUtil;
import com.sunlight.client.util.FXUtil;
import com.sunlight.client.vo.*;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class MainWindowController implements Initializable {
    public static final String STATUS_FAILURE = "失败";
    public static final String STATUS_SUCCESS = "成功";
    private static Logger logger = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    TextField wipnoField;
    @FXML
    TableView<PackingInfo> tableView;
    @FXML
    TableColumn<PackingInfo, String> columnId;
    @FXML
    TableColumn<PackingInfo, String> columnName;
    @FXML
    TableColumn<PackingInfo, String> columnDescription;
    @FXML
    TableColumn<PackingInfo, String> columnText;
    @FXML
    TableColumn<PackingInfo, String> columnSequece;
    @FXML
    TableColumn<PackingInfo, String> columnInterval;
    @FXML
    Label statusLabel;
    @FXML
    Label packageSNField;
    @FXML
    Label progressField;

    private TaharaService taharaService;
    private ResourceBundle bundle;
    private String side;
    private String scannerSide;
    private long packageSequence;

    private ClientConfiguration clientConfiguration;

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        logger.info("[MainWindowController.initialize] location : {}", location);

        this.bundle = bundle;

        ApplicationContext context = ClientApplication.getContext();

        this.taharaService = context.getBean(TaharaService.class);

        logger.info("taharaService : {}", this.taharaService);

        Environment env = context.getBean(Environment.class);

        logger.info("env : {}", env);

        side = env.getProperty("client.side");

        logger.info("side : {}", side);

        scannerSide = env.getProperty("client.scannerSide");

        logger.info("scannerSide : {}", scannerSide);

        this.tableView.setRowFactory(row -> {
            return new TableRow<PackingInfo>() {
                @Override
                protected void updateItem(PackingInfo item, boolean empty) {
                    super.updateItem(item, empty);
                }
            };
        });

        this.columnDescription.setCellFactory(cel -> {
            return new TableCell<PackingInfo, String> () {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(item);

                    if(STATUS_SUCCESS.equals(item)) {
                        getTableRow().setStyle("-fx-background-color: #90EE90;");
                    } else if(STATUS_FAILURE.equals(item)) {
                        getTableRow().setStyle("-fx-background-color: #FF0F0F;");
                    } else {
                        getTableRow().setStyle("");
                    }
                }
            };
        });

        this.wipnoField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    wipnoField.selectAll();
                }
            }
        });

        this.taharaService.registerExceptionHandler((Exception ex) -> {
            Platform.runLater(() -> {
                FXUtil.alert(bundle.getString("error"), String.format(bundle.getString("exceptionOccurred"), ex.getMessage()));
            });
        });

        this.clientConfiguration = ClientConfigurationUtil.getClientConfiguration();

        if(!StringUtils.isEmpty(this.clientConfiguration.getPackingSN())) {
            this.packageSNField.setText(this.clientConfiguration.getPackingSN());
        }

        this.packageSequence = this.clientConfiguration.getPackageSequence();

        logger.info("packageSequence : {}", packageSequence);
    }

    //---- EVENT HANDLERs ----

    public void onWipFieldKeyPress(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            do5018();
        }
    }

    public void onTableMousePressed(MouseEvent event) {
        PackingInfo row = tableView.getSelectionModel().getSelectedItem();

        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            wipnoField.setText(row.getWipno());
        } else if(event.isSecondaryButtonDown()) {
            ContextMenu cm = new ContextMenu();

            if(STATUS_FAILURE.equals(row.getStatus())) {
                MenuItem deleteMenuItem = new MenuItem("删除");
                deleteMenuItem.setOnAction((ActionEvent evt) -> {
                    if(FXUtil.warningChoice(this.bundle.getString("warningDelete"), this.bundle.getString("deleteIsDangerous"))) {
                        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
                        refreshStatusBar();
                    }
                });
                cm.getItems().add(deleteMenuItem);

//                MenuItem resendMenuItem = new MenuItem("重发");
//                resendMenuItem.setOnAction((ActionEvent evt) -> {
//                    PackingInfo selected = tableView.getSelectionModel().getSelectedItem();
//
//                    wipnoField.setText(selected.getWipno());
//
//                    do5018();
//
//                    refreshStatusBar();
//                });
//                cm.getItems().add(resendMenuItem);
            }

            MenuItem propertyMenuItem = new MenuItem("属性");
            propertyMenuItem.setOnAction((ActionEvent evt) -> {
                showProperties();
            });
            cm.getItems().add(propertyMenuItem);

            MenuItem deleteMenuItem = new MenuItem("出箱");
            deleteMenuItem.setOnAction((ActionEvent evt) -> {
                do10010(tableView.getSelectionModel().getSelectedItem().getWipno());
            });
            cm.getItems().add(deleteMenuItem);

            cm.show(tableView, event.getScreenX(), event.getScreenY());
        }
    }

    public void onTableKeyPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            showProperties();
        }
    }

    public void onBtnNewPackingClicked(MouseEvent mouseEvent) {
        String sn = packageSNField.getText().trim();

        if(!StringUtils.isEmpty(sn)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("alreadyAssigned"));
            return;
        }

        do10009();
    }

    public void onBtnClosePackingClicked(MouseEvent mouseEvent) {
        String sn = packageSNField.getText().trim();
        ObservableList<PackingInfo> infoList = tableView.getItems();

        int count = infoList.size();
        int success = infoList.filtered(o -> STATUS_SUCCESS.equals(o.getStatus())).size();

        if(success < count) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("failureExisted"));
            return;
        }

        do10012("2");
    }

    //---- BUSINESS ACTIONS ----

    private void do5018() {
        final String sn = packageSNField.getText().trim();

        if(StringUtils.isEmpty(sn)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("snEmpty"));
            return;
        }

        final String wipno = wipnoField.getText().trim();
        logger.info("[MainWindowController.wipFieldKeyPress] wipno : {}", wipno);
        wipnoField.setText("");

        if(StringUtils.isEmpty(wipno)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("wipnoEmpty"));
            return;
        }

        final String equipmentName = this.taharaService.getEquipmentName();

        List<PackingInfo> existed = tableView.getItems().filtered(o -> wipno.equals(o.getWipno()));

        PackingInfo tmp = null;

        if(existed.size() == 0) {
            tmp = new PackingInfo();
            tmp.setSequence(String.format("%d", ++packageSequence));
            tableView.getItems().add(tmp);
        } else {
            tmp = existed.get(0);
        }

        final PackingInfo packingInfo = tmp;

        packingInfo.setStatus("预捡中");
        packingInfo.setRequest(null);
        packingInfo.setResponse(null);
        packingInfo.setStart(System.currentTimeMillis());

        Observable.create((ObservableOnSubscribe<PackingInfo>) (emitter) -> {
            String transactionId = String.format("%s-%d", equipmentName, System.currentTimeMillis());

            Message message = new Message();

            message.setBody(new Body());
            message.setHeader(new Header());
            message.getHeader().setLocation(new Location());
            message.getBody().setPackageInfo(new PackageInfo());
            message.getBody().setPcb(new PCB());

            message.getHeader().setMessageClass("5018");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(transactionId);
            message.getHeader().getLocation().setRouteName(equipmentName);
            message.getHeader().getLocation().setEquipmentName(equipmentName);
            message.getHeader().getLocation().setZoneName(equipmentName);

            message.getBody().getPackageInfo().setSn(sn);
            message.getBody().getPcb().setBarcode(wipno);
            message.getBody().getPcb().setLabel(wipno);
            message.getBody().getPcb().setModelCode(wipno);
            message.getBody().getPcb().setPcbSide(this.side);
            message.getBody().getPcb().setScannerMountSide(this.scannerSide);
            message.getBody().getPcb().setSerialNo(packingInfo.getSequence());

//            message.getHeader().setMessageClass("501");
//            message.getHeader().setReply(1);
//            message.getHeader().setTransactionID(transactionId);
//            message.getHeader().getLocation().setEquipmentName(equipmentName);
//            message.getHeader().getLocation().setRouteName(equipmentName);
//            message.getHeader().getLocation().setZoneName(equipmentName);
//
//            message.getBody().getPcb().setBarcode(wipno);
//            message.getBody().getPcb().setModelCode(wipno);
//            message.getBody().getPcb().setSerialNo(wipno);
//            message.getBody().getPcb().setPcbSide(this.side);
//            message.getBody().getPcb().setScannerMountSide(this.scannerSide);

            packingInfo.setRequest(message);

            emitter.onNext(packingInfo);
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.io())
        .subscribe(packingInfo1 -> {
            try {
                taharaService.process5018(packingInfo1.getRequest(), (Message result) -> {
                    logger.info("{} :: {}", packingInfo1.getRequest().getHeader().getTransactionID(), result.getHeader().getTransactionID());

                    if(!packingInfo1.getRequest().getHeader().getTransactionID().equals(result.getHeader().getTransactionID())) {
                        return;
                    }

                    packingInfo1.setEnd(System.currentTimeMillis());
                    packingInfo1.setResponse(result);
                    packingInfo1.setStatus(result != null ? ("0".equals(result.getBody().getResult().getErrorCode()) ? STATUS_SUCCESS : STATUS_FAILURE) : "预捡中");

                    Platform.runLater(() -> {
                        tableView.refresh();
                        tableView.scrollTo(tableView.getItems().size() - 1);

                        refreshStatusBar();

                        if(STATUS_FAILURE.equals(packingInfo1.getStatus())) {
                            //TODO: Maybe play a short mp3 sound will be better.
                            Toolkit.getDefaultToolkit().beep();

                            showFailureDialog(packingInfo1.getWipno(), packingInfo1.getText());
                        }
                    });

                    this.clientConfiguration.setPackageSequence(this.packageSequence);
                    ClientConfigurationUtil.saveClientConfiguration(this.clientConfiguration.getEquipmentName(), this.clientConfiguration.getUsername(), this.clientConfiguration.getProductNumber(), this.clientConfiguration.getProductBIN(), this.clientConfiguration.getPackingSN(), this.clientConfiguration.getPackageSequence());
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                Platform.runLater(() -> {
                    FXUtil.alert(bundle.getString("error"), String.format(bundle.getString("exceptionOccurred"), ex.getMessage()));
                });
            }
        });
    }

    private void do10009() {
        final String equipmentName = this.taharaService.getEquipmentName();

        final String productNumber = FXUtil.input(this.bundle.getString("inputProductNumber"), this.bundle.getString("inputProductNumber"), this.clientConfiguration.getProductNumber());

        if(StringUtils.isEmpty(productNumber)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("productNumberEmpty"));
            return;
        }

        final String productBin = FXUtil.input(this.bundle.getString("inputProductBIN"), this.bundle.getString("inputProductBIN"), this.clientConfiguration.getProductBIN());

        if(StringUtils.isEmpty(productBin)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("productBINEmpty"));
            return;
        }

        Observable.create((ObservableOnSubscribe<Message>) (emitter) -> {
            String transactionId = String.format("%s-%d", equipmentName, System.currentTimeMillis());

            this.clientConfiguration.setProductNumber(productNumber);
            this.clientConfiguration.setProductBIN(productBin);
            ClientConfigurationUtil.saveClientConfiguration(this.clientConfiguration.getEquipmentName(), this.clientConfiguration.getUsername(), this.clientConfiguration.getProductNumber(), this.clientConfiguration.getProductBIN());

            Message message = new Message();

            message.setBody(new Body());
            message.setHeader(new Header());
            message.getHeader().setLocation(new Location());
            message.getBody().setProduct(new Product());

            message.getHeader().setMessageClass("10009");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(transactionId);
            message.getHeader().getLocation().setRouteName(equipmentName);
            message.getHeader().getLocation().setEquipmentName(equipmentName);
            message.getHeader().getLocation().setZoneName(equipmentName);

            message.getBody().getProduct().setName(productNumber);
            message.getBody().getProduct().setBin(productBin);

            emitter.onNext(message);
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.io())
        .subscribe(request -> {
            try {
                taharaService.startPacking(request, (Message result) -> {
                    logger.info("{} :: {}", request.getHeader().getTransactionID(), result.getHeader().getTransactionID());

                    if(!request.getHeader().getTransactionID().equals(result.getHeader().getTransactionID())) {
                        return;
                    }

                    Platform.runLater(() -> {
                        if(!"0".equals(result.getBody().getResult().getErrorCode())) {
                            FXUtil.error(this.bundle.getString("error"), String.format(this.bundle.getString("errorOccurred"), result.getBody().getResult().getErrorCode(), result.getBody().getResult().getErrorText()));
                            packageSNField.setText("");
                            return;
                        }

                        packageSNField.setText(result.getBody().getPackageContainer().getNumber());
                        packageSequence = 0;

                        this.clientConfiguration.setPackageSequence(packageSequence);
                        this.clientConfiguration.setPackingSN(result.getBody().getPackageContainer().getNumber());
                        ClientConfigurationUtil.saveClientConfiguration(this.clientConfiguration.getEquipmentName(), this.clientConfiguration.getUsername(), this.clientConfiguration.getProductNumber(), this.clientConfiguration.getProductBIN(), this.clientConfiguration.getPackingSN(), this.clientConfiguration.getPackageSequence());
                    });
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                Platform.runLater(() -> {
                    FXUtil.alert(bundle.getString("error"), String.format(bundle.getString("exceptionOccurred"), ex.getMessage()));
                });
            }
        });
    }

    private void do10010(String wipno) {
        final String sn = packageSNField.getText().trim();

        if(StringUtils.isEmpty(sn)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("snEmpty"));
            return;
        }

        logger.info("[MainWindowController.do10010] wipno : {}", wipno);

        if(StringUtils.isEmpty(wipno)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("wipnoEmpty"));
            return;
        }

        final String equipmentName = this.taharaService.getEquipmentName();

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
            message.getBody().getPackageContainer().setType("0"); // 0 - 出箱， 1 - 入箱。
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
                            FXUtil.error(this.bundle.getString("error"), String.format(this.bundle.getString("errorOccurred"), result.getBody().getResult().getErrorCode(), result.getBody().getResult().getErrorText()));
                            packageSNField.setText("");
                            return;
                        }

                        tableView.getItems().removeAll(tableView.getItems().filtered(packingInfo -> wipno.equals(packingInfo.getWipno())));

                        tableView.refresh();
                        //tableView.scrollTo(tableView.getItems().size() - 1);

                        refreshStatusBar();
                    });
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                Platform.runLater(() -> {
                    FXUtil.alert(bundle.getString("error"), String.format(bundle.getString("exceptionOccurred"), ex.getMessage()));
                });
            }
        });
    }

    private void do10012(String state) { //箱子状态 = 0 : 装箱中, 1 : 装完, 2 : 封箱, 3 : 报废
        final String equipmentName = this.taharaService.getEquipmentName();

        Observable.create((ObservableOnSubscribe<Message>) (emitter) -> {
            String transactionId = String.format("%s-%d", equipmentName, System.currentTimeMillis());

            Message message = new Message();

            message.setBody(new Body());
            message.setHeader(new Header());
            message.getHeader().setLocation(new Location());
            message.getBody().setProduct(new Product());
            message.getBody().setPackageContainer(new PackageContainer());

            message.getHeader().setMessageClass("10012");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(transactionId);
            message.getHeader().getLocation().setRouteName(equipmentName);
            message.getHeader().getLocation().setEquipmentName(equipmentName);
            message.getHeader().getLocation().setZoneName(equipmentName);

            message.getBody().getProduct().setNumber(this.clientConfiguration.getProductNumber());
            message.getBody().getProduct().setLabel("");

            message.getBody().getPackageContainer().setNumber(packageSNField.getText());
            message.getBody().getPackageContainer().setState(state);

            emitter.onNext(message);
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.io())
        .subscribe(request -> {
            try {
                taharaService.closePacking(request, (Message result) -> {
                    logger.info("{} :: {}", request.getHeader().getTransactionID(), result.getHeader().getTransactionID());

                    if(!request.getHeader().getTransactionID().equals(result.getHeader().getTransactionID())) {
                        return;
                    }

                    Platform.runLater(() -> {
                        if(!"0".equals(result.getBody().getResult().getErrorCode())) {
                            FXUtil.error(this.bundle.getString("error"), String.format(this.bundle.getString("errorOccurred"), result.getBody().getResult().getErrorCode(), result.getBody().getResult().getErrorText()));
                            return;
                        }

                        packageSNField.setText("");
                        tableView.getItems().clear();
                    });
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                Platform.runLater(() -> {
                    FXUtil.alert(bundle.getString("error"), String.format(bundle.getString("exceptionOccurred"), ex.getMessage()));
                });
            }
        });
    }

    //---- UTILITIES ----

    private void refreshStatusBar() {
        ObservableList<PackingInfo> infoList = tableView.getItems();

        int count = infoList.size();
        int inProgress = infoList.filtered(o -> o.getResponse() == null).size();
        int success = infoList.filtered(o -> STATUS_SUCCESS.equals(o.getStatus())).size();
        int failure = infoList.filtered(o -> STATUS_FAILURE.equals(o.getStatus())).size();

        statusLabel.setText(String.format(this.bundle.getString("statusBarMessage"), count, inProgress, success, failure));

        progressField.setText(String.format("%d/%d", success, count));

        if(failure > 0) {
            statusLabel.setStyle("-fx-background-color: #FF0F0F; -fx-border-color: #0FFFFF;");
        } else {
            statusLabel.setStyle("-fx-border-color: #AAAAAA; -fx-border-width: 2.00; -fx-border-style: solid;");
        }
    }

    private void showProperties() {
        PackingInfo row = tableView.getSelectionModel().getSelectedItem();

        FXUtil.alert(this.bundle.getString("info"), String.format(this.bundle.getString("tableRowInfo"), row.getWipno(), row.getText()));
    }

    private void showFailureDialog(String wipno, String errorMessage) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("fx.bundle.Failure");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/Failure.fxml"), bundle);

            Parent root = loader.load();

            FailureWindowController controller = loader.getController();

            controller.setWipno(wipno);
            controller.setErrorMessage(errorMessage);

            Stage stage = new Stage();

            stage.setTitle(bundle.getString("title"));
            stage.setScene(new Scene(root));

            stage.getScene().setUserData(wipno);

            stage.initOwner(tableView.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            logger.info("result : {}", controller.getResult());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
