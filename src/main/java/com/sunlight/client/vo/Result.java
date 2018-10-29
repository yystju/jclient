package com.sunlight.client.vo;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "barcode",
        "errorCode",
        "errorText",
        "actionCode"
})
public class Result implements Serializable {
    @XmlAttribute(name = "barcode", required = false)
    private String barcode;

    @XmlAttribute(name = "errorCode", required = false)
    private String errorCode;

    @XmlAttribute(name = "errorText", required = false)
    private String errorText;

    @XmlAttribute(name = "actionCode", required = false)
    private String actionCode;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }
}
