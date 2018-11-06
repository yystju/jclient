package com.sunlight.client.vo;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "body")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "packageInfo",
        "pcb",
        "result",
        "product",
        "packageContainer"
})
public class Body implements Serializable {
    @XmlElement(name="packageInfo", required=true)
    private PackageInfo packageInfo;

    @XmlElement(name="pcb", required=true)
    private PCB pcb;

    @XmlElement(name="result", required=true)
    private Result result;

    @XmlElement(name="product", required=true)
    private Product product;

    @XmlElement(name="packageContainer", required=true)
    private PackageContainer packageContainer;

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public PCB getPcb() {
        return pcb;
    }

    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PackageContainer getPackageContainer() {
        return packageContainer;
    }

    public void setPackageContainer(PackageContainer packageContainer) {
        this.packageContainer = packageContainer;
    }
}
