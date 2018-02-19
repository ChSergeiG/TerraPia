package ru.chsergeyg.terrapia.server.runnable;

import com.sun.net.httpserver.HttpServer;
import ru.chsergeyg.terrapia.server.HandlerEnum;
import ru.chsergeyg.terrapia.server.Init;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPDRunnable implements Runnable {

    private static Map<String, String> credentials;

    @Override
    public void run() {
        Init.getLogger(getClass().getName()).info("HTTPDRunnable started");
        credentials = new HashMap<>();
        credentials.put("root", "password");
        credentials.put("groot", "groot im am");
        try {
            final HttpServer httpServer = HttpServer.create(new InetSocketAddress(Init.HTTPD_PORT), 0);
            Arrays.asList(HandlerEnum.values()).forEach((e) -> httpServer.createContext(e.getUrl(), e.getHandler()));
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (Exception e) {
            Init.getLogger(getClass().getName()).warning(e.getMessage());
        }
    }

    public static boolean isUserValid(String login, String passwd) {
        if (credentials.size() > 0 && credentials.containsKey(login))
            return credentials.get(login).equals(passwd);
        return false;
    }
}
