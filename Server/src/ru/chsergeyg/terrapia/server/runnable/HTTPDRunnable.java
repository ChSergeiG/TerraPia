package ru.chsergeyg.terrapia.server.runnable;

import com.sun.net.httpserver.HttpServer;
import ru.chsergeyg.terrapia.server.HandlerFactory;
import ru.chsergeyg.terrapia.server.Init;

import java.io.IOException;
import java.net.InetSocketAddress;
<<<<<<< HEAD
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;
=======
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
>>>>>>> d706eca... auth^

public class HTTPDRunnable implements Runnable {

    private static Map<String, String> credentials;

    @Override
    public void run() {
        Init.getLogger(getClass().getName()).info("HTTPDRunnable started");
        credentials = new HashMap<>();
        credentials.put("root", "password");
        credentials.put("groot", "groot im am");
        try {
<<<<<<< HEAD
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(Init.SERVER_PORT), 0);
            myCreateContext(httpServer, "/", "/echoHeader", "/echoGet", "/echoPost", "/favicon.ico");
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            Init.getLogger(getClass().getName()).warning(e.toString());
=======
            final HttpServer httpServer = HttpServer.create(new InetSocketAddress(Init.HTTPD_PORT), 0);
            Arrays.asList(HandlerEnum.values()).forEach((e) -> httpServer.createContext(e.getUrl(), e.getHandler()));
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (Exception e) {
            Init.getLogger(getClass().getName()).warning(e.getMessage());
>>>>>>> d706eca... auth^
        }
    }

    public static boolean isUserValid(String login, String passwd) {
        if (credentials.size() > 0 && credentials.containsKey(login))
            return credentials.get(login).equals(passwd);
        return false;
    }
<<<<<<< HEAD

    private void myCreateContext(HttpServer server, String... path) {
        Stream.of(path).forEach((e) -> server.createContext(e, HandlerFactory.getHandler(e)));
    }

=======
>>>>>>> d706eca... auth^
}
