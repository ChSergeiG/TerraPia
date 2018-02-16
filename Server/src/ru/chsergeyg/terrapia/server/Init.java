package ru.chsergeyg.terrapia.server;

import ru.chsergeyg.terrapia.server.runnable.HTTPDRunnable;
import ru.chsergeyg.terrapia.server.runnable.PiRunnable;
import ru.chsergeyg.terrapia.server.runnable.SerialRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Init {

    public static final int SERVER_PORT = 8080;
    private static Map<String, Logger> LOGGER;
    private static boolean initialized = false;

    static Collection<Thread> threads;

    Init() {
        init();
        threads = new ArrayList<>();
        threads.add(new Thread(null, new SerialRunnable(), "Serial thread"));
        threads.add(new Thread(null, new HTTPDRunnable(), "HTTPD thread"));
        threads.add(new Thread(null, new PiRunnable(), "Pi4j thread"));
        threads.forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException exc) {
                Init.getLogger(Server.class.getName()).warning(exc.toString());
            }
        });
        threads.forEach(Thread::start);
    }

    public static Logger getLogger(String name) {
        return LOGGER.get(name);
    }

    public static Map<String, Logger> getLogger() {
        return LOGGER;
    }

    private static void init() {
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
                    HandlerFactory.class.getName(),
                    Logger.getLogger(HandlerFactory.class.getName()));
            LOGGER.put(
                    Server.class.getName(),
                    Logger.getLogger(Server.class.getName()));
        }
        initialized = true;
    }
}
