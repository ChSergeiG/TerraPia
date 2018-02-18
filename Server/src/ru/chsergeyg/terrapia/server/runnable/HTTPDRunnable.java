package ru.chsergeyg.terrapia.server.runnable;

import com.sun.net.httpserver.HttpServer;
import ru.chsergeyg.terrapia.server.HandlerEnum;
import ru.chsergeyg.terrapia.server.Init;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPDRunnable implements Runnable {

    private static Map<String, String> credentials;

    @Override
    public void run() {
        Init.getLogger(getClass().getName()).info("HTTPDRunnable started");
        credentials = new HashMap<>();
        credentials.put("root", getHash("password"));
        credentials.put("groot", getHash("groot im am"));
        try {
            final HttpServer httpServer = HttpServer.create(new InetSocketAddress(Init.HTTPD_PORT), 0);
            List.of(HandlerEnum.values()).forEach((e) -> httpServer.createContext(e.getUrl(), e.getHandler()));
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (Exception e) {
            Init.getLogger(getClass().getName()).warning("NPE on context creation procedure");
        }
    }

    private String getHash(String value) {
        byte[] hash = new byte[0];
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes());
        } catch (NoSuchAlgorithmException e) {
            Init.getLogger(getClass().getName()).warning(e.toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hash) {
            stringBuilder.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return stringBuilder.toString();
    }

    public static boolean isUserValid(String login, String shaPasswd) {
        if (credentials.size() > 0 && credentials.containsKey(login))
            return credentials.get(login).equals(shaPasswd);
        return false;
    }
}
