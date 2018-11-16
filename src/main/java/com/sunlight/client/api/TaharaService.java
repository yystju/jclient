package com.sunlight.client.api;

import com.sunlight.client.vo.Message;

public interface TaharaService {
    boolean authenticate(String username, String passwd);

    void setEquipmentName(String equipmentName);
    String getEquipmentName();

    void process10009(Message request, MessageCallback callback) throws Exception;

    void process5018(Message request, MessageCallback callback) throws Exception;

    void process10010(Message request, MessageCallback callback) throws Exception;
    void process10011(Message request, MessageCallback callback) throws Exception;
    void process10012(Message request, MessageCallback callback) throws Exception;

    void registerExceptionHandler(ErrorHandler errorHandler);
}
