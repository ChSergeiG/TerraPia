package ru.chsergeyg.terrapia.server;

import ru.chsergeyg.terrapia.server.runnable.HTTPDRunnable;
import ru.chsergeyg.terrapia.server.runnable.PiRunnable;
import ru.chsergeyg.terrapia.server.runnable.SerialRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Init {

    private static Map<String, Logger> LOGGER;
    private static boolean initialized = false;

    public static Logger getLogger(String name) {
        return LOGGER.get(name);
    }

    public static Map<String, Logger> getLogger() {
        return LOGGER;
    }

    public static void init() {
        if (!initialized) {
            LOGGER = new HashMap<>();
            LOGGER.put(
                    HTTPDRunnable.class.getName(),
                    Logger.getLogger(HTTPDRunnable.class.getName()));
            LOGGER.put(
                    SerialRunnable.class.getName(),
                    Logger.getLogger(SerialRunnable.class.getName()));
            LOGGER.put(
                    PiRunnable.class.getName(),
                    Logger.getLogger(PiRunnable.class.getName()));
            LOGGER.put(
                    Server.class.getName(),
                    Logger.getLogger(Server.class.getName()));
        }
        initialized = true;
    }
}
