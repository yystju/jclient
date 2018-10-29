package com.sunlight.client.vo;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "routeID",
        "routeName",
        "equipmentID",
        "equipmentName",
        "zoneID",
        "zonePos",
        "zoneName",
        "laneNo",
        "controllerGuid",
})
public class Location implements Serializable {
    @XmlAttribute(name = "routeID", required = false)
    private String routeID;

    @XmlAttribute(name = "routeName", required = false)
    private String routeName;

    @XmlAttribute(name = "equipmentID", required = false)
    private String equipmentID;

    @XmlAttribute(name = "equipmentName", required = false)
    private String equipmentName;

    @XmlAttribute(name = "zoneID", required = false)
    private String zoneID;

    @XmlAttribute(name = "zonePos", required = false)
    private String zonePos;

    @XmlAttribute(name = "zoneName", required = false)
    private String zoneName;

    @XmlAttribute(name = "laneNo", required = false)
    private String laneNo;

    @XmlAttribute(name = "controllerGuid", required = false)
    private String controllerGuid;

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public String getZonePos() {
        return zonePos;
    }

    public void setZonePos(String zonePos) {
        this.zonePos = zonePos;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(String laneNo) {
        this.laneNo = laneNo;
    }

    public String getControllerGuid() {
        return controllerGuid;
    }

    public void setControllerGuid(String controllerGuid) {
        this.controllerGuid = controllerGuid;
    }
}
