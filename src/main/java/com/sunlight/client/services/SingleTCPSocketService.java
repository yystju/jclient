package com.sunlight.client.services;

import com.sunlight.client.api.ErrorHandler;
import com.sunlight.client.api.MessageCallback;
import com.sunlight.client.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Scope("singleton")
public class SingleTCPSocketService {
    private static Logger logger = LoggerFactory.getLogger(SingleTCPSocketService.class);

    @Value("${tahara.host}")
    String host;

    @Value("${tahara.port}")
    Integer port;

    private ConcurrentHashMap<String, MessageCallback> callbackMap = new ConcurrentHashMap<>();

    JAXBContext context;

    private Socket socket;
    private Thread socketReceiverThread;

    private ExecutorService callbackPool;

    private ErrorHandler errorHandler;

    @PostConstruct
    public void init() throws IOException, JAXBException {
        context = JAXBContext.newInstance("com.sunlight.client.vo");
        callbackPool = Executors.newFixedThreadPool(5);
    }

    @PreDestroy
    public void dispose() {
        callbackPool.shutdown();

        try {
            socket.close();
        } catch (Exception e) {
            logger.info("Dispose socket exception : {}", e.getMessage());
        }

        logger.info("[SingleTCPSocketService.dispose]");
    }

    public void sendMessage(Message message, MessageCallback callback) throws Exception {
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        ByteArrayOutputStream outs = new ByteArrayOutputStream();

        marshaller.marshal(message, outs);

        byte[] array = outs.toByteArray();

        outs.close();

        String content = String.format("\2%d\1%s\3", array.length, new String(array, "UTF-8"));

        logger.info("[sendMessage] content : {}", content);

        callbackMap.put(message.getHeader().getTransactionID(), callback);

        getSocket().getOutputStream().write(content.getBytes("UTF-8"));
        getSocket().getOutputStream().flush();
    }

    public void registerExceptionHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }


    protected Socket getSocket() throws IOException {
        if(socket == null || !socket.isConnected()) {
            if(socket != null) {
                socket.close();
            }

            logger.info("Connecting {}:{}...", host, port);

            socket = new Socket();

            socket.setTcpNoDelay(true);
            socket.setSoTimeout(60 * 1000);
            socket.setKeepAlive(true);
            socket.connect(new InetSocketAddress(host, port));

            socketReceiverThread = new Thread(() -> {
                byte[] buffer = new byte[1024 * 1024 * 8];
                int pos = 0;

                byte[] buf = new byte[1024 * 8];
                int len = -1;

                while(this.socket != null && !this.socket.isClosed() && this.socket.isConnected()) {
                    try {
                        while(-1 != (len = this.socket.getInputStream().read(buf))) {
                            System.arraycopy(buf, 0, buffer, pos, len);
                            pos += len;

                            int endIdx = indexOf(buffer, (byte)'\3', 0, pos);

                            if(endIdx > -1) {
                                int startIdx = indexOf(buffer, (byte)'\2', 0, endIdx);

                                if(startIdx > -1) {
                                    int headIdx = indexOf(buffer, (byte)'\1', startIdx + 1, endIdx);

                                    if(headIdx > -1) {
                                        String lenStr = new String(buffer, startIdx + 1, headIdx).trim();

                                        int packingLength = Integer.parseInt(lenStr);

                                        if(packingLength != (endIdx - headIdx - 1)) {
                                            logger.error("MISMATCH!!!");
                                        } else {
                                            String str = new String(buffer, headIdx + 1, packingLength).trim();

                                            if(str != null) {
                                                processMessage(str);
                                            }
                                        }

                                        pos = pos - endIdx - 1;
                                        System.arraycopy(buffer, endIdx + 1, buffer, 0, pos);
                                    }
                                }
                            }
                        }
                    } catch (UnmarshalException ex) {
                        try {
                            logger.error(ex.getMessage(), ex);

                            this.socket.close();

                            if(this.errorHandler != null) {
                                this.errorHandler.onException((Exception) ex.getCause());
                            }
                        } catch (IOException e) {
                        }

                        this.socket = null;

                        break;
                    } catch (SocketTimeoutException ex) {
                        try {
                            logger.error(ex.getMessage(), ex);

                            this.socket.close();
                        } catch (IOException e) {
                        }

                        this.socket = null;

                        break;
                    } catch (Exception ex) {
                        try {
                            logger.error(ex.getMessage(), ex);

                            this.socket.close();

                            if(this.errorHandler != null) {
                                this.errorHandler.onException(ex);
                            }
                        } catch (IOException e) {
                        }

                        this.socket = null;

                        break;
                    }
                }
            });

            socketReceiverThread.start();
        }

        return socket;
    }

    private void processMessage(String str) throws JAXBException {
        logger.info("[processMessage] received message : {}", str);

        Unmarshaller unmarshaller = context.createUnmarshaller();
        Message message = (Message) unmarshaller.unmarshal(new StringReader(str));

        String transactionId = message.getHeader().getTransactionID();

        MessageCallback callback = callbackMap.get(transactionId);

        if(callback != null) {
            callbackPool.submit(() -> {
               callback.onReceived(message);
            });
        }
    }

    private static int indexOf(byte[] data, byte target, int start, int end) {
        for(int i = start; i < end; ++i) {
            if(data[i] == target) {
                return i;
            }
        }

        return -1;
    }
}
