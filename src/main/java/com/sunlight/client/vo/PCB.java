package com.sunlight.client.vo;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "pcb")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "barcode",
        "label",
        "modelCode",
        "serialNo",
        "pcbSide",
        "scannerMountSide",
})
public class PCB implements Serializable {
    @XmlAttribute(name = "barcode", required = false)
    private String barcode;

    @XmlAttribute(name = "label", required = false)
    private String label;

    @XmlAttribute(name = "modelCode", required = false)
    private String modelCode;

    @XmlAttribute(name = "serialNo", required = false)
    private String serialNo;

    @XmlAttribute(name = "pcbSide", required = false)
    private String pcbSide;

    @XmlAttribute(name = "scannerMountSide", required = false)
    private String scannerMountSide;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getPcbSide() {
        return pcbSide;
    }

    public void setPcbSide(String pcbSide) {
        this.pcbSide = pcbSide;
    }

    public String getScannerMountSide() {
        return scannerMountSide;
    }

    public void setScannerMountSide(String scannerMountSide) {
        this.scannerMountSide = scannerMountSide;
    }
}
