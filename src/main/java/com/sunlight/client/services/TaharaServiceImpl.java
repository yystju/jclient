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
    public void startPacking(Message request, MessageCallback callback) {

    }

    @Override
    public void closePacking(Message request, MessageCallback callback) {

    }

    @Override
    public void registerExceptionHandler(ErrorHandler errorHandler) {
        this.singleTCPSocketService.registerExceptionHandler(errorHandler);
    }

    //    private int count = 0;

    @Override
    public void process5018(Message request, MessageCallback callback) throws Exception {
//        Random r = new Random(System.currentTimeMillis());
//        try {
//            Thread.sleep(500 + (long)(700.00 * r.nextDouble()));
//        } catch (InterruptedException e) {
//        }
//
//        Message result = new Message();
//
//        result.setBody(new Body());
//        result.setHeader(new Header());
//        result.getHeader().setLocation(new Location());
//        result.getBody().setPackageInfo(new com.sunlight.client.vo.PackageInfo());
//        result.getBody().setResult(new Result());
//
//        result.getHeader().setTransactionID(request.getHeader().getTransactionID());
//        result.getHeader().setMessageClass("5018");
//        result.getHeader().setReply(1);
//
//        int tmp = 0;
//
//        synchronized(TaharaServiceImpl.class) {
//            tmp = count++;
//        }
//
//        result.getBody().setResult(new Result());
//        result.getBody().getResult().setBarcode(request.getBody().getPcb().getBarcode());
//        result.getBody().getResult().setErrorCode(tmp % 2 == 0 ? "0" : "1");
//        result.getBody().getResult().setErrorText(tmp % 2 == 0 ? "valid" : "invalid");
//
//        callback.onReceived(result);

        singleTCPSocketService.sendMessage(request, callback);
    }

}
