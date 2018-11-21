package com.sunlight.client.gui.fx.controllers;

import com.sunlight.client.ClientApplication;
import com.sunlight.client.api.MessageCallback;
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
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    public static final String STATUS_INIT = "预捡中";
    public static final String STATUS_FAILURE = "失败";
    public static final String STATUS_SUCCESS = "成功";
    public static final String STATUS_PRCEEDED = "已处理";

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
    TextField packageSNField;
    @FXML
    TextField progressField;

    private TaharaService taharaService;
    private ResourceBundle bundle;
    private String side;
    private String scannerSide;

    private long packageSequence;

    private long packageQuantity;
    private long packageCapacity;

//    private long inprogressCounter;
//    private long successCounter;
//    private long failedCounter;
//    private long proceededCounter;

    private Map<String, String> statusMap = new HashMap<>();

    private ClientConfiguration clientConfiguration;
    private Stage finishPackingWaitingDialog;

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
                    } else if(STATUS_PRCEEDED.equals(item)) {
                        getTableRow().setStyle("-fx-background-color: #9090EE;");
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

        clearPage();

        this.clientConfiguration = ClientConfigurationUtil.getClientConfiguration();

        Platform.runLater(() -> {
            if(!StringUtils.isEmpty(this.clientConfiguration.getPackingSN())) {
                this.packageSNField.setText(this.clientConfiguration.getPackingSN());

                do10011(null);
            }
        });
    }

    //---- EVENT HANDLERs ----

    public void onWipFieldKeyPress(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            do5018(null);
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
                        refreshStatusInfo();
                    }
                });
                cm.getItems().add(deleteMenuItem);

                MenuItem proceededMenuItem = new MenuItem("已处理");
                proceededMenuItem.setOnAction((ActionEvent evt) -> {
                    tableView.getSelectionModel().getSelectedItem().setStatus(STATUS_PRCEEDED);
                    tableView.refresh();
                    refreshStatusInfo();
//                    this.proceededCounter++;
                    statusMap.put(tableView.getSelectionModel().getSelectedItem().getWipno(), STATUS_PRCEEDED);
                });
                cm.getItems().add(proceededMenuItem);
            }

//            MenuItem propertyMenuItem = new MenuItem("属性");
//            propertyMenuItem.setOnAction((ActionEvent evt) -> {
//                showProperties();
//            });
//            cm.getItems().add(propertyMenuItem);
//
//            MenuItem deleteMenuItem = new MenuItem("出箱");
//            deleteMenuItem.setOnAction((ActionEvent evt) -> {
//                do10010(tableView.getSelectionModel().getSelectedItem().getWipno());
//            });
//            cm.getItems().add(deleteMenuItem);
//
            cm.show(tableView, event.getScreenX(), event.getScreenY());
        }
    }

    public void onTableKeyPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            showProperties();
        }
    }

    public void onBtnFinishPackingClicked(MouseEvent mouseEvent) {
        logger.info("[MainWindowController.onBtnClosePackingClicked]");
        String sn = packageSNField.getText().trim();
        ObservableList<PackingInfo> infoList = tableView.getItems();

        int count = infoList.size();
        int ok = infoList.filtered(o -> STATUS_SUCCESS.equals(o.getStatus()) || STATUS_PRCEEDED.equals(o.getStatus())).size();

        if(ok < count) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("failureExisted"));
            return;
        }

        do10012("1");
    }

    public void onBtnPullOutClicked(MouseEvent mouseEvent) {
        logger.info("[MainWindowController.onBtnPullOutClicked]");

        String wipno = FXUtil.input(this.bundle.getString("pullOutInputTitle"), this.bundle.getString("pullOutInputInfo"), wipnoField.getText());

        if(wipno != null) {
            do10010(wipno);
        }
    }

    public void onBtnUnPackingClicked(MouseEvent mouseEvent) {
        logger.info("[MainWindowController.onBtnUnPackingClicked]");
        do10012("3");
    }

    public void onBtnOpenPackingClicked(MouseEvent mouseEvent) {
        logger.info("[MainWindowController.onBtnOpenPackingClicked]");
        String sn = packageSNField.getText().trim();

        if(!StringUtils.isEmpty(sn)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("failureExistedWhileOpening"));
            return;
        }

        do10012("0");
    }

    public void onBtnFetchBackClicked(MouseEvent mouseEvent) {
        logger.info("[MainWindowController.onBtnFetchBackClicked]");

        String sn = packageSNField.getText();

        if(StringUtils.isEmpty(sn)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("snEmpty"));
            return;
        }

        do10011(null);
    }

//    public void onBtnNewPackingClicked(MouseEvent mouseEvent) {
//        logger.info("[MainWindowController.onBtnNewPackingClicked]");
//        String sn = packageSNField.getText().trim();
//
//        if(!StringUtils.isEmpty(sn)) {
//            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("alreadyAssigned"));
//            return;
//        }
//
//        do10009();
//    }

//    public void onBtnSealPackingClicked(MouseEvent mouseEvent) {
//        logger.info("[MainWindowController.onBtnSealPackingClicked]");
//        String sn = packageSNField.getText().trim();
//        ObservableList<PackingInfo> infoList = tableView.getItems();
//
//        int count = infoList.size();
//        int success = infoList.filtered(o -> STATUS_SUCCESS.equals(o.getStatus())).size();
//
//        if(success < count) {
//            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("failureExisted"));
//            return;
//        }
//
//        do10012("2");
//    }

//    public void onBtnClearClicked(MouseEvent mouseEvent) {
//        logger.info("[MainWindowController.onBtnClearClicked]");
//        if(FXUtil.warningChoice(this.bundle.getString("warningClear"), this.bundle.getString("clearIsDangerous"))) {
//            this.packageSNField.setText("");
//            this.progressField.setText("");
//            this.packageSequence = 0;
//            this.clientConfiguration.setPackingSN("");
//            ClientConfigurationUtil.saveClientConfiguration(this.clientConfiguration.getEquipmentName(), this.clientConfiguration.getUsername(), this.clientConfiguration.getProductNumber(), this.clientConfiguration.getProductBIN(), this.clientConfiguration.getPackingSN(), this.clientConfiguration.getPackageSequence());
//
//            refreshStatusInfo();
//        }
//    }

    //---- BUSINESS ACTIONS ----

    private void do5018(MessageCallback callback) {
        final String sn = packageSNField.getText().trim();
        final String wipno = wipnoField.getText().trim();

        logger.info("[MainWindowController.do5018] sn : {}, wipno : {}", sn, wipno);

        if(StringUtils.isEmpty(sn)) {
            //TODO: Need to change this to fetching product number and BIN from MES...
            if(!StringUtils.isEmpty(wipno) && wipno.length() >= 26) {
                String productName = wipno.substring(0, 15);
                String BIN = wipno.substring(15, 18);

                logger.info("[MainWindowController.do5018] productName : {}, BIN : {}", productName, BIN);

                do10009(productName, BIN, result -> {
                    do10011(result1 -> {
                        do5018(callback);
                    });
                });

                return;
            }


            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("snEmpty"));
            return;
        }

        wipnoField.setText("");

        if(StringUtils.isEmpty(wipno)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("wipnoEmpty"));
            return;
        }

        final String equipmentName = this.clientConfiguration.getEquipmentName();

        final PackingInfo packingInfo = new PackingInfo();
        packingInfo.setStatus(STATUS_INIT);
        packingInfo.setRequest(null);
        packingInfo.setResponse(null);
        packingInfo.setSequence(String.format("%d", ++packageSequence));
        packingInfo.setStart(System.currentTimeMillis());

//        ++inprogressCounter;
//        logger.info("inprogressCounter : {}, successCounter : {}, failedCounter : {}, proceededCounter : {}", inprogressCounter, successCounter, failedCounter, proceededCounter);

        if(!STATUS_SUCCESS.equals(statusMap.get(wipno))) {
            statusMap.put(wipno, STATUS_INIT);
        }
        long inprogress = statusMap.entrySet().stream().filter(e -> e.getValue().equals(STATUS_INIT)).count() + this.packageQuantity;

//        if(inprogressCounter >= this.packageCapacity) {
        if(inprogress >= this.packageCapacity) {
            logger.info("Met Package Capacity {} (inprogressCounter : {}). Will block the new input and trigger finishing packing operation...", this.packageCapacity, inprogress);

            if(this.finishPackingWaitingDialog == null) {
                this.finishPackingWaitingDialog = openWaitingDialog();
            }
        }

        tableView.getItems().add(packingInfo);

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
            message.getBody().getPcb().setSerialNo(String.format("%d", inprogress));

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
                    packingInfo1.setStatus(result != null ? ("0".equals(result.getBody().getResult().getErrorCode()) ? STATUS_SUCCESS : STATUS_FAILURE) : STATUS_INIT);

                    if(STATUS_SUCCESS.equals(packingInfo1.getStatus())) {
//                        successCounter++;
                        statusMap.put(wipno, packingInfo1.getStatus());

                        long quantity = this.packageQuantity + statusMap.entrySet().stream().filter(e -> e.getValue().equals(STATUS_SUCCESS)).count();

                        logger.info("quantity : {}", quantity);

                        if(quantity == this.packageCapacity) {
                            do10012("1");
                        }
                    } else if(STATUS_FAILURE.equals(packingInfo1.getStatus())) {
//                        failedCounter++;
                    }

                    logger.info("statusMap : {}", statusMap);

                    if(callback != null) {
                        callback.onReceived(result);
                    }

                    Platform.runLater(() -> {
                        tableView.refresh();
                        tableView.scrollTo(tableView.getItems().size() - 1);

                        refreshStatusInfo();

                        if(STATUS_FAILURE.equals(packingInfo1.getStatus())) {
                            //TODO: Maybe play a short mp3 sound will be better.
                            Toolkit.getDefaultToolkit().beep();
                            showFailureDialog(packingInfo1);
                        }
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

    private void do10009(String productNumber, String productBin, MessageCallback callback) {
        final String equipmentName = this.clientConfiguration.getEquipmentName();

//        final String productNumber = FXUtil.input(this.bundle.getString("inputProductNumber"), this.bundle.getString("inputProductNumber"), this.clientConfiguration.getProductNumber());

        if(StringUtils.isEmpty(productNumber)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("productNumberEmpty"));
            return;
        }

//        final String productBin = FXUtil.input(this.bundle.getString("inputProductBIN"), this.bundle.getString("inputProductBIN"), this.clientConfiguration.getProductBIN());

        if(StringUtils.isEmpty(productBin)) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("productBINEmpty"));
            return;
        }

        Observable.create((ObservableOnSubscribe<Message>) (emitter) -> {
            String transactionId = String.format("%s-%d", equipmentName, System.currentTimeMillis());

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
                taharaService.process10009(request, (Message result) -> {
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

                        this.clientConfiguration.setPackingSN(result.getBody().getPackageContainer().getNumber());
                        ClientConfigurationUtil.saveClientConfiguration();

                        if(callback != null) {
                            callback.onReceived(result);
                        }
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

        final String equipmentName = this.clientConfiguration.getEquipmentName();

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
                            return;
                        }

                        tableView.getItems().filtered(packingInfo -> wipno.equals(packingInfo.getWipno())).forEach(packingInfo -> {
//                            this.proceededCounter++;
                            packingInfo.setStatus(STATUS_PRCEEDED);
                            statusMap.put(packingInfo.getWipno(), packingInfo.getStatus());
                        });

                        tableView.refresh();
                        refreshStatusInfo();
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

    private void do10011(MessageCallback callback) {
        final String equipmentName = this.clientConfiguration.getEquipmentName();

        final String sn = packageSNField.getText();

        if(this.finishPackingWaitingDialog == null) {
            this.finishPackingWaitingDialog = openWaitingDialog();
        }

        Observable.create((ObservableOnSubscribe<Message>) (emitter) -> {
            String transactionId = String.format("%s-%d", equipmentName, System.currentTimeMillis());

            Message message = new Message();

            message.setBody(new Body());
            message.setHeader(new Header());
            message.getHeader().setLocation(new Location());
            message.getBody().setProduct(new Product());
            message.getBody().setPackageContainer(new PackageContainer());

            message.getHeader().setMessageClass("10011");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(transactionId);
            message.getHeader().getLocation().setRouteName(equipmentName);
            message.getHeader().getLocation().setEquipmentName(equipmentName);
            message.getHeader().getLocation().setZoneName(equipmentName);

//            message.getBody().getProduct().setNumber(finalProductNumber);
            message.getBody().getPackageContainer().setNumber(sn);

            emitter.onNext(message);
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.io())
        .subscribe(request -> {
            try {
                taharaService.process10011(request, (Message result) -> {
                    logger.info("{} :: {}", request.getHeader().getTransactionID(), result.getHeader().getTransactionID());

                    if(!request.getHeader().getTransactionID().equals(result.getHeader().getTransactionID())) {
                        return;
                    }

                    Platform.runLater(() -> {
                        if(!"0".equals(result.getBody().getResult().getErrorCode())) {
                            FXUtil.error(this.bundle.getString("error"), String.format(this.bundle.getString("errorOccurred"), result.getBody().getResult().getErrorCode(), result.getBody().getResult().getErrorText()));
                            return;
                        }

                        String quantityStr = result.getBody().getPackageContainer().getQuantity();
                        String capacityStr = result.getBody().getPackageContainer().getCapacity();

                        try {
                            this.packageQuantity = Long.parseLong(quantityStr);
                            this.packageCapacity = Long.parseLong(capacityStr);
                        } catch (NumberFormatException ex) {
                            this.packageQuantity = 0;
                            this.packageCapacity = 0;
                        }

                        logger.info("this.packageQuantity : {}, this.packageCapacity : {}", this.packageQuantity, this.packageCapacity);

                        refreshStatusInfo();

                        if(callback != null) {
                            callback.onReceived(result);
                        }

                        Platform.runLater(() -> {
                            if(this.finishPackingWaitingDialog != null) {
                                this.finishPackingWaitingDialog.getScene().getWindow().hide();
                            }
                        });
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

    private void do10012(String state) { //箱子状态 = 0 : 装箱中, 1 : 装完, 2 : 封箱, 3 : 报废 // 3就是拆箱。
        final String equipmentName = this.clientConfiguration.getEquipmentName();

        String sn = packageSNField.getText();

        if("3".equals(state)) {
            String ret = FXUtil.input(this.bundle.getString("unpackingInputTitle"), this.bundle.getString("unpackingInputInfo"), sn);

            if(ret != null) {
                sn = ret;
            } else {
                return;
            }
        } else if ("0".equals(state)) {
            String ret = FXUtil.input(this.bundle.getString("openingInputTitle"), this.bundle.getString("openingInputInfo"), sn);

            if(ret != null) {
                sn = ret;
            } else {
                return;
            }
        }

        final String finalSN = sn;

        if(this.finishPackingWaitingDialog == null) {
            this.finishPackingWaitingDialog = openWaitingDialog();
        }

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

            message.getBody().getProduct().setNumber("");
            message.getBody().getProduct().setLabel("");

            message.getBody().getPackageContainer().setNumber(finalSN);
            message.getBody().getPackageContainer().setState(state);

            emitter.onNext(message);
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.io())
        .subscribe(request -> {
            try {
                taharaService.process10012(request, (Message result) -> {
                    logger.info("{} :: {}", request.getHeader().getTransactionID(), result.getHeader().getTransactionID());

                    if(!request.getHeader().getTransactionID().equals(result.getHeader().getTransactionID())) {
                        return;
                    }

                    Platform.runLater(() -> {
                        if(this.finishPackingWaitingDialog != null) {
                            this.finishPackingWaitingDialog.getScene().getWindow().hide();
                        }

                        if(!"0".equals(result.getBody().getResult().getErrorCode())) {
                            FXUtil.error(this.bundle.getString("error"), String.format(this.bundle.getString("errorOccurred"), result.getBody().getResult().getErrorCode(), result.getBody().getResult().getErrorText()));
                            return;
                        }

                        if("1".equals(state) || "3".equals(state)) {
                            this.clientConfiguration.setPackingSN("");
                            ClientConfigurationUtil.saveClientConfiguration();
                            clearPage();
                        }
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

    private void refreshStatusInfo() {
        ObservableList<PackingInfo> infoList = tableView.getItems();

        int count = infoList.size();
//        int inProgress = infoList.filtered(o -> STATUS_INIT.equals(o.getStatus())).size();
        int success = infoList.filtered(o -> STATUS_SUCCESS.equals(o.getStatus())).size();
        int failure = infoList.filtered(o -> STATUS_FAILURE.equals(o.getStatus())).size();
        int proceeded = infoList.filtered(o -> STATUS_PRCEEDED.equals(o.getStatus())).size();

        statusLabel.setText(String.format(this.bundle.getString("statusBarMessage"), count, success, failure, proceeded));

        progressField.setText(String.format("%d/%d", packageQuantity + statusMap.entrySet().stream().filter(e -> e.getValue().equals(STATUS_SUCCESS)).count(), packageCapacity));

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

    private void showFailureDialog(PackingInfo packingInfo) {
        String wipno = packingInfo.getWipno();
        String errorMessage = packingInfo.getText();

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("fx.bundle.Failure");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/Failure.fxml"), bundle);

            Parent root = loader.load();

            FailureWindowController controller = loader.getController();

            controller.setWipno(wipno);
            controller.setErrorMessage(errorMessage);
            controller.setClientConfiguration(clientConfiguration);

            Stage stage = new Stage();

            stage.setTitle(bundle.getString("title"));
            stage.setScene(new Scene(root));

            stage.initOwner(tableView.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            logger.info("result : {}", controller.getResult());

            if(wipno.equals(controller.getResult())) {
                packingInfo.setStatus(STATUS_PRCEEDED);
//                proceededCounter++;
                statusMap.put(wipno, STATUS_PRCEEDED);
                tableView.refresh();
                refreshStatusInfo();
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Stage openWaitingDialog() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("fx.bundle.InProgress");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/InProgress.fxml"), bundle);

            Parent root = loader.load();

            InProgressWindowController controller = loader.getController();

            Stage stage = new Stage(StageStyle.UNDECORATED);

            stage.setScene(new Scene(root));

            stage.initOwner(tableView.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            return stage;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private void clearPage() {
        this.packageSNField.setText("");

        this.tableView.getItems().clear();
        this.tableView.refresh();

        this.packageQuantity = 0;
        this.packageCapacity = 0;
        this.packageSequence = 0;

//        this.inprogressCounter = 0;
//        this.successCounter = 0;
//        this.failedCounter = 0;
//        this.proceededCounter = 0;

        statusMap.clear();

        refreshStatusInfo();
    }
}
