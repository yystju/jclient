package com.sunlight.client.api;

import com.sunlight.client.vo.Message;

public interface TaharaService {
    boolean authenticate(String username, String passwd);

    void setEquipmentName(String equipmentName);
    String getEquipmentName();

    void startPacking(Message request, MessageCallback callback);
    void closePacking(Message request, MessageCallback callback);

    void process5018(Message request, MessageCallback callback) throws Exception;

    void registerExceptionHandler(ErrorHandler errorHandler);
}
