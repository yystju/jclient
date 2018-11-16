package com.sunlight.client.services;

import com.sunlight.client.api.ErrorHandler;
import com.sunlight.client.api.MessageCallback;
import com.sunlight.client.api.TaharaService;
import com.sunlight.client.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaharaServiceImpl implements TaharaService {
    private static Logger logger = LoggerFactory.getLogger(TaharaServiceImpl.class);

    @Autowired
    SingleTCPSocketService singleTCPSocketService;

    private String equipmentName;

    @Override
    public boolean authenticate(String username, String passwd) {
        return true;
    }

    @Override
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    @Override
    public String getEquipmentName() {
        return this.equipmentName;
    }

    @Override
    public void process10009(Message request, MessageCallback callback) throws Exception {
        singleTCPSocketService.sendMessage(request, callback);
    }

    @Override
    public void process10012(Message request, MessageCallback callback) throws Exception {
        singleTCPSocketService.sendMessage(request, callback);
    }

    @Override
    public void registerExceptionHandler(ErrorHandler errorHandler) {
        this.singleTCPSocketService.registerExceptionHandler(errorHandler);
    }

    @Override
    public void process5018(Message request, MessageCallback callback) throws Exception {
        singleTCPSocketService.sendMessage(request, callback);
    }

    @Override
    public void process10010(Message request, MessageCallback callback) throws Exception {
        singleTCPSocketService.sendMessage(request, callback);
    }

    @Override
    public void process10011(Message request, MessageCallback callback) throws Exception {
        singleTCPSocketService.sendMessage(request, callback);
    }
}
