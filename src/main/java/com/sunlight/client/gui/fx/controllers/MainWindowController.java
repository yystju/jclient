package com.sunlight.client.gui.fx.controllers;

import com.sunlight.client.ClientApplication;
import com.sunlight.client.api.TaharaService;
import com.sunlight.client.gui.fx.vo.PackingInfo;
import com.sunlight.client.util.FXUtil;
import com.sunlight.client.vo.*;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
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
    Label statusLabel;
    @FXML
    Label packageSNField;

    private TaharaService taharaService;
    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        logger.info("[MainWindowController.initialize] location : {}", location);

        this.bundle = bundle;

        ApplicationContext context = ClientApplication.getContext();

        this.taharaService = context.getBean(TaharaService.class);

        logger.info("taharaService : {}", this.taharaService);

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

                    if("成功".equals(item)) {
                        getTableRow().setStyle("-fx-background-color: #90EE90;");
                    } else if("失败".equals(item)) {
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
    }

    //---- EVENT HANDLERs ----

    public void onWipFieldKeyPress(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            do5018();
        }
    }

    public void onTableMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            PackingInfo row = tableView.getSelectionModel().getSelectedItem();
            wipnoField.setText(row.getWipno());
        } else if(event.isSecondaryButtonDown()) {
            ContextMenu cm = new ContextMenu();

            MenuItem deleteMenuItem = new MenuItem("删除");
            deleteMenuItem.setOnAction((ActionEvent evt) -> {
                tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
                refreshStatusBar();
            });
            cm.getItems().add(deleteMenuItem);

            MenuItem resendMenuItem = new MenuItem("重发");
            resendMenuItem.setOnAction((ActionEvent evt) -> {
                PackingInfo selected = tableView.getSelectionModel().getSelectedItem();

                wipnoField.setText(selected.getWipno());

                do5018();

                refreshStatusBar();
            });
            cm.getItems().add(resendMenuItem);

            MenuItem propertyMenuItem = new MenuItem("属性");
            propertyMenuItem.setOnAction((ActionEvent evt) -> {
                showProperties();
            });
            cm.getItems().add(propertyMenuItem);

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

        //TODO: 改成真正的调用后端代码。。。

        packageSNField.setText("1010101");
    }

    public void onBtnClosePackingClicked(MouseEvent mouseEvent) {
        String sn = packageSNField.getText().trim();
        ObservableList<PackingInfo> infoList = tableView.getItems();

        int count = infoList.size();
        int success = infoList.filtered(o -> "成功".equals(o.getStatus())).size();

        if(success < count) {
            FXUtil.alert(this.bundle.getString("error"), this.bundle.getString("failureExisted"));
            return;
        }

        //TODO: 改成真正的调用后端代码。。。

        packageSNField.setText("");
        tableView.getItems().clear();
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
            tableView.getItems().add(tmp);
        } else {
            tmp = existed.get(0);
        }

        final PackingInfo packingInfo = tmp;

        packingInfo.setStatus("预捡中");
        packingInfo.setRequest(null);
        packingInfo.setResponse(null);

        Observable.create((ObservableOnSubscribe<PackingInfo>) (emitter) -> {
            String transactionId = String.format("%s-%d", equipmentName, System.currentTimeMillis());

            Message message = new Message();

            message.setBody(new Body());
            message.setHeader(new Header());
            message.getHeader().setLocation(new Location());
            message.getBody().setPackageInfo(new PackageInfo());
            message.getBody().setPcb(new PCB());
//
//                message.getHeader().setMessageClass("5018");
//                message.getHeader().setReply(1);
//                message.getHeader().setTransactionID(transactionId);
//
//                message.getBody().getPackageInfo().setSn(sn);
//                message.getBody().getPcb().setBarcode(wipno);
//                message.getBody().getPcb().setLabel(wipno);
//                message.getBody().getPcb().setModelCode(wipno);
//                message.getBody().getPcb().setPcbSide("2");
//                message.getBody().getPcb().setScannerMountSide("2");
//                message.getBody().getPcb().setSerialNo(wipno);


            message.getHeader().setMessageClass("501");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(transactionId);
            message.getHeader().getLocation().setEquipmentName(equipmentName);
            message.getHeader().getLocation().setRouteName(equipmentName);
            message.getHeader().getLocation().setZoneName(equipmentName);

            message.getBody().getPcb().setBarcode(wipno);
            message.getBody().getPcb().setModelCode(wipno);
            message.getBody().getPcb().setSerialNo(wipno);
            message.getBody().getPcb().setPcbSide("1");
            message.getBody().getPcb().setScannerMountSide("T");

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

                    packingInfo1.setResponse(result);
                    packingInfo1.setStatus(result != null ? ("0".equals(result.getBody().getResult().getErrorCode()) ? "成功" : "失败") : "预捡中");

                    Platform.runLater(() -> {
                        tableView.refresh();

                        refreshStatusBar();

                        if("失败".equals(packingInfo1.getStatus())) {
                            Toolkit.getDefaultToolkit().beep();
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

    private void refreshStatusBar() {
        ObservableList<PackingInfo> infoList = tableView.getItems();

        int count = infoList.size();
        int inProgress = infoList.filtered(o -> o.getResponse() == null).size();
        int success = infoList.filtered(o -> "成功".equals(o.getStatus())).size();
        int failure = infoList.filtered(o -> "失败".equals(o.getStatus())).size();

        statusLabel.setText(String.format("总共%d个，预捡中%d个，成功%d个，失败%d个", count, inProgress, success, failure));

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
}
