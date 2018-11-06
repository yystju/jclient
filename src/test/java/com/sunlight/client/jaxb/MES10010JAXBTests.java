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

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
@SpringBootTest
public class MES10010JAXBTests {
    private static Logger logger = LoggerFactory.getLogger(MES10010JAXBTests.class);
    @Test
    public void test10010RequestLoad() {
        try {
            JAXBContext context = JAXBContext.newInstance("com.sunlight.client.vo");

            Unmarshaller unmarshaller = context.createUnmarshaller();

            InputStream ins = MES10010JAXBTests.class.getResourceAsStream("/xmls/10010_request.xml");

            Message message = (Message) unmarshaller.unmarshal(new StreamSource(ins));

            assertEquals("10010", message.getHeader().getMessageClass());

            logger.info("transactionid : {}", message.getHeader().getTransactionID());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Test
    public void test10010RequestDump() {
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
            message.getBody().setProduct(new Product());
            message.getBody().setPackageContainer(new PackageContainer());

            message.getHeader().setMessageClass("10010");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(String.format("%s-%d-%d", equipmentName, System.currentTimeMillis(), seq));

            message.getBody().getProduct().setNumber("1111");
            message.getBody().getProduct().setLabel("1111");
            message.getBody().getProduct().setSide("1");

            message.getBody().getPackageContainer().setNumber("2222");
            message.getBody().getPackageContainer().setType("1");
            message.getBody().getPackageContainer().setOperator("...");

            marshaller.marshal(message, outs);

            String content = outs.toString("UTF-8");

            outs.close();

            logger.info("content : {}", content);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Test
    public void test10010ResponseLoad() {
        try {
            JAXBContext context = JAXBContext.newInstance("com.sunlight.client.vo");

            Unmarshaller unmarshaller = context.createUnmarshaller();

            InputStream ins = MES10010JAXBTests.class.getResourceAsStream("/xmls/10010_response.xml");

            Message message = (Message) unmarshaller.unmarshal(new StreamSource(ins));

            assertEquals("10010", message.getHeader().getMessageClass());

            logger.info("transactionid : {}", message.getHeader().getTransactionID());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Test
    public void test10010ResponseDump() {
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
            message.getBody().setResult(new Result());

            message.getHeader().setMessageClass("10010");
            message.getHeader().setReply(1);
            message.getHeader().setTransactionID(String.format("%s-%d-%d", equipmentName, System.currentTimeMillis(), seq));

            message.getBody().getResult().setBarcode(barcode);
            message.getBody().getResult().setErrorText("");
            message.getBody().getResult().setErrorCode("0");

            marshaller.marshal(message, outs);

            String content = outs.toString("UTF-8");

            outs.close();

            logger.info("content : {}", content);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
