package com.sunlight.client.vo;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "header")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "messageClass",
        "transactionID",
        "reply",
        "location"
})
public class Header implements Serializable {
    @XmlAttribute(name = "messageClass", required = false)
    private String messageClass;

    @XmlAttribute(name = "transactionID", required = false)
    private String transactionID;

    @XmlAttribute(name = "reply", required = false)
    private int reply;

    @XmlElement(name="location", required=true)
    private Location location;

    public String getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(String messageClass) {
        this.messageClass = messageClass;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

