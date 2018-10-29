package com.sunlight.client.vo;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public ObjectFactory() {
    }

    public Message createMessage() {
        return new Message();
    }


    public Body createBody() {
        return new Body();
    }

    public Header createHeader() {
        return new Header();
    }

    public Location createLocation() {
        return new Location();
    }

    public PackageInfo createPackageInfo() {
        return new PackageInfo();
    }

    public PCB createPCB() {
        return new PCB();
    }

    public Result createResult() {
        return new Result();
    }
}
