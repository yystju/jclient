package com.sunlight.client.gui.fx.vo;

import com.sunlight.client.vo.Message;

public class PackingInfo {
    private String sequence;
    private String status;

    private long start;
    private long end;

    private Message request;
    private Message response;

    public String getTransactionId() {
        return this.request != null ? this.request.getHeader().getTransactionID() : null;
    }

    public String getWipno() {
        return this.request != null ? this.request.getBody().getPcb().getBarcode() : null;
    }

    public String getInterval() {
        return end > start ? String.format("%f", ((float)(end - start)) / 1000.0f) : "-";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return this.response != null ? String.format("%s: %s", this.response.getBody().getResult().getErrorCode(), this.response.getBody().getResult().getErrorText()) : null;
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

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
