package ru.chsergeyg.terrapia.server;

import javafx.concurrent.Worker;
import ru.chsergeyg.terrapia.server.runnable.HTTPDRunnable;
import ru.chsergeyg.terrapia.server.runnable.PiRunnable;
import ru.chsergeyg.terrapia.server.runnable.SerialRunnable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Init {

    private static Map<String, Logger> LOGGER;
    private static boolean initialized = false;
    public static Path WORKING_PATH;

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
            String runPath = System.getProperty("java.class.path").split(";")[0];
            WORKING_PATH = runPath.endsWith(".jar") ? Paths.get(runPath).getParent() : Paths.get(runPath).getParent().getParent();
            LOGGER = new HashMap<>();
            threads = new ArrayList<>();
            logClasses(Init.class, HTTPDRunnable.class, SerialRunnable.class, PiRunnable.class, Server.class,
                    HandlerEnum.class, HandlerStorage.class, StringBuilder.class);
            getLogger(getClass().getName()).info("Working directory: " + WORKING_PATH);
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

    private void initThreads(Runnable... runnable) {
        Stream.of(runnable).forEach((r) -> threads.add(new Thread(null, r, r.toString())));
    }

    private void logClasses(Class... classes) {
        Stream.of(classes).forEach((c) -> LOGGER.put(c.getName(), Logger.getLogger(c.getName())));
    }
}
