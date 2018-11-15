package com.sunlight.client.gui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

@SpringBootTest
@RunWith(JUnit4.class)
public class GUIBot {
    private static Logger logger = LoggerFactory.getLogger(GUIBot.class);

    @Test
    public void test() {
        try {
            Robot robot = new Robot();

            robot.setAutoDelay(1000);
            robot.setAutoWaitForIdle(true);

            robot.mouseMove(100, 100);
            robot.mouseMove(200, 200);
        } catch (AWTException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
