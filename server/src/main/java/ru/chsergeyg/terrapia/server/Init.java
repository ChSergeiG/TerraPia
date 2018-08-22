package ru.chsergeyg.terrapia.server;

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

public class Init {

<<<<<<< HEAD
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
=======
    public static final int HTTPD_PORT = 8080;
    public static final String PAGEBUILDER_DELIMITER = "~@~";
    public static Path WORKING_PATH;
    private static Map<String, Logger> LOGGER;
    private static boolean initialized = false;

    Collection<Thread> threads;

    Init() {
>>>>>>> d706eca... auth^
        if (!initialized) {
            String runPath = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0];
            WORKING_PATH = runPath.endsWith(".jar") ? Paths.get(runPath).getParent() : Paths.get(runPath).getParent().getParent();
            LOGGER = new HashMap<>();
<<<<<<< HEAD
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
=======
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

    public static Logger getLogger(String name) {
        return LOGGER.get(LOGGER.containsKey(name) ? name : Thread.currentThread().getName());
    }

    private void initThreads(Runnable... runnable) {
        Stream.of(runnable).forEach((r) -> threads.add(new Thread(null, r, r.toString())));
    }

    private void logClasses(Class... classes) {
        Stream.of(classes).forEach((c) -> LOGGER.put(c.getName(), Logger.getLogger(c.getName())));
    }
>>>>>>> d706eca... auth^
}
