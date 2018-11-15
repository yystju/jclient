package com.sunlight.client.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunlight.client.gui.fx.vo.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ClientConfigurationUtil {
    private static Logger logger = LoggerFactory.getLogger(ClientConfigurationUtil.class);

    private static ClientConfiguration clientConfiguration;

    public static ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    public static void loadClientConfiguration() {
        File clientConfigurationFile = new File("./client.json");

        ObjectMapper mapper = new ObjectMapper();

        if(clientConfigurationFile.exists()) {
            try {
                clientConfiguration = mapper.readValue(clientConfigurationFile, ClientConfiguration.class);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if(clientConfiguration == null) {
            clientConfiguration = new ClientConfiguration();
        }
    }

    public static void saveClientConfiguration(String equipmentName, String username) {
        File clientConfigurationFile = new File("./client.json");

        ObjectMapper mapper = new ObjectMapper();

        try {
            clientConfiguration.setEquipmentName(equipmentName);
            clientConfiguration.setUsername(username);

            mapper.writeValue(clientConfigurationFile, clientConfiguration);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void saveClientConfiguration(String equipmentName, String username, String productNumber, String productBIN) {
        File clientConfigurationFile = new File("./client.json");

        ObjectMapper mapper = new ObjectMapper();

        try {
            clientConfiguration.setEquipmentName(equipmentName);
            clientConfiguration.setUsername(username);
            clientConfiguration.setProductNumber(productNumber);
            clientConfiguration.setProductBIN(productBIN);

            mapper.writeValue(clientConfigurationFile, clientConfiguration);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void saveClientConfiguration(String equipmentName, String username, String productNumber, String productBIN, String packingSN, long packageSequence) {
        File clientConfigurationFile = new File("./client.json");

        ObjectMapper mapper = new ObjectMapper();

        try {
            clientConfiguration.setEquipmentName(equipmentName);
            clientConfiguration.setUsername(username);
            clientConfiguration.setProductNumber(productNumber);
            clientConfiguration.setProductBIN(productBIN);
            clientConfiguration.setPackingSN(packingSN);

            mapper.writeValue(clientConfigurationFile, clientConfiguration);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
