package ru.chsergeyg.terrapia.server;

import ru.chsergeyg.terrapia.server.runnable.HTTPDRunnable;
import ru.chsergeyg.terrapia.server.runnable.PiRunnable;
import ru.chsergeyg.terrapia.server.runnable.SerialRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Init {

    private static Map<String, Logger> LOGGER;
    private static boolean initialized = false;

    public Collection<Thread> threads;
    public static final int HTTPD_PORT = 8080;
    public static final String PAGEBUILDER_DELIMITER = "~@~";

    Init() {
        init();
    }

    public static Logger getLogger(String name) {
        return LOGGER.get(LOGGER.containsKey(name) ? name : Thread.currentThread().getName());
    }

    private void init() {
        if (!initialized) {
            LOGGER = new HashMap<>();
            threads = new ArrayList<>();
            logClasses(Init.class, HTTPDRunnable.class, SerialRunnable.class, PiRunnable.class, Server.class,
                    HandlerEnum.class, HandlerStorage.class, StringBuilder.class);
            initThreads(new SerialRunnable(), new HTTPDRunnable(), new PiRunnable());
            threads.forEach((t) -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    Init.getLogger(Server.class.getName()).warning(e.toString());
                }
            });
            threads.forEach(Thread::start);
        }
        initialized = true;
    }

    private void initThreads(Runnable... runnables) {
        Stream.of(runnables).forEach((r) -> threads.add(new Thread(null, r, r.toString())));
    }

    private void logClasses(Class... classes) {
        Stream.of(classes).forEach((c) -> LOGGER.put(c.getName(), Logger.getLogger(c.getName())));
    }
}
