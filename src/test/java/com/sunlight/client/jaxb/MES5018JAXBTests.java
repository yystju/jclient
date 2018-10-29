package com.sunlight.client.jaxb;

import com.sunlight.client.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
@SpringBootTest
public class MES5018JAXBTests {
    private static Logger logger = LoggerFactory.getLogger(MES5018JAXBTests.class);
    @Test
    public void test5018Load() {
        try {
            JAXBContext context = JAXBContext.newInstance("com.sunlight.client.vo");

            Unmarshaller unmarshaller = context.createUnmarshaller();

            InputStream ins = MES5018JAXBTests.class.getResourceAsStream("/xmls/5018_request.xml");

            Message message = (Message) unmarshaller.unmarshal(new StreamSource(ins));

            assertEquals("5018", message.getHeader().getMessageClass());
            assertEquals("1234567890&xxxx", message.getHeader().getTransactionID());
            assertEquals("TEST-DEV11", message.getHeader().getLocation().getEquipmentName());

            logger.info("transactionid : {}", message.getHeader().getTransactionID());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Test
    public void test5018Dump() {
        try {
            JAXBContext context = JAXBContext.newInstance("com.sunlight.client.vo");

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            ByteArrayOutputStream outs = new ByteArrayOutputStream();

            String equipmentName = "S01-L01-NPM-M1";
            int seq = 1;
            String sn = "123456";
            String barcode = "654321";
            String modelCode = "654";

            Message message = new Message();

            message.setBody(new Body());
            message.setHeader(new Header());
            message.getHeader().setLocation(new Location());
            message.getBody().setPackageInfo(new PackageInfo());
            message.getBody().setPcb(new PCB());

            message.getHeader().setMessageClass("5018");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(String.format("%s-%d-%d", equipmentName, System.currentTimeMillis(), seq));

            message.getBody().getPackageInfo().setSn(sn);
            message.getBody().getPcb().setBarcode(barcode);
            message.getBody().getPcb().setLabel(barcode);
            message.getBody().getPcb().setModelCode(modelCode);
            message.getBody().getPcb().setPcbSide("2");
            message.getBody().getPcb().setScannerMountSide("2");
            message.getBody().getPcb().setSerialNo(barcode);

            marshaller.marshal(message, outs);

            String content = outs.toString("UTF-8");

            outs.close();

            logger.info("content : {}", content);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
