package ru.chsergeyg.terrapia.arduino.compiler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    private static final Logger logger = LoggerFactory.getLogger(PropertyReader.class);

    private static Properties props;

    private static synchronized void init() {
        props = new Properties();
        try {
            props.load(PropertyReader.class.getClassLoader().getResourceAsStream("terr.properties"));
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static String getProperty(String key) {
        check();
        return props.getProperty(key);
    }


    private static void check() {
        if (props == null) {
            init();
        }
    }

    public static synchronized String readString(String key) {
        return getProperty(key);
    }

}