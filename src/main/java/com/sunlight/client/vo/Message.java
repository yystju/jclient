package com.sunlight.client.vo;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "header",
        "body"
})
public class Message implements Serializable {
    @XmlElement(name="header", required=true)
    private Header header;

    @XmlElement(name="body", required=true)
    private Body body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
