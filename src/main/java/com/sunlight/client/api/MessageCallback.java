package com.sunlight.client.api;

import com.sunlight.client.vo.Message;

public interface MessageCallback {
    void onReceived(Message result);
}
