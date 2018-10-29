package com.sunlight.client.gui.fx.vo;

import com.sunlight.client.vo.Message;

public class PackingInfo {
    private String status;

    private Message request;
    private Message response;

    public String getTransactionId() {
        return this.request != null ? this.request.getHeader().getTransactionID() : null;
    }

    public String getWipno() {
        return this.request != null ? this.request.getBody().getPcb().getBarcode() : null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return this.response != null ? this.response.getBody().getResult().getErrorText() : null;
    }

    public Message getRequest() {
        return request;
    }

    public void setRequest(Message request) {
        this.request = request;
    }

    public Message getResponse() {
        return response;
    }

    public void setResponse(Message response) {
        this.response = response;
    }
}
