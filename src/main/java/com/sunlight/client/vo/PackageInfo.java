package com.sunlight.client.vo;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "packageInfo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "sn"
})
public class PackageInfo implements Serializable {
    @XmlElement(name="sn", required=true)
    private String sn;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
