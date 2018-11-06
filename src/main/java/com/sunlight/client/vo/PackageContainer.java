package com.sunlight.client.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "packageContainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class PackageContainer {
    @XmlAttribute(name = "number", required = false)
    private String number;

    @XmlAttribute(name = "label", required = false)
    private String label;

    @XmlAttribute(name = "capacity", required = false)
    private String capacity;

    @XmlAttribute(name = "quantity", required = false)
    private String quantity;

    @XmlAttribute(name = "state", required = false)
    private String state;

    @XmlAttribute(name = "type", required = false)
    private String type;

    @XmlAttribute(name = "operator", required = false)
    private String operator;

    @XmlAttribute(name = "size", required = false)
    private String size;

    @XmlAttribute(name = "product", required = false)
    private String product;

    @XmlAttribute(name = "bin", required = false)
    private String bin;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }
}
