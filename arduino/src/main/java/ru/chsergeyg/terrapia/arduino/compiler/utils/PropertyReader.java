package ru.chsergeyg.terrapia.arduino.compiler.utils;

import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    private static Properties props;

    private static synchronized void init() {
        props = new Properties();
        try {
            props.load(PropertyReader.class.getClassLoader().getResourceAsStream("terr.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized String readValue(String key) {
        if (props == null) {
            init();
        }
        return props.getProperty(key);
    }
}