package ru.chsergeyg.terrapia.server.runnable;

import com.sun.net.httpserver.HttpServer;
import ru.chsergeyg.terrapia.server.HandlerFactory;
import ru.chsergeyg.terrapia.server.Init;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

public class HTTPDRunnable implements Runnable {

    private static final String LOGIN = "root";
    private static final String PASS_HASH = "982f249c728f730cbe5ddd7b02a3c3e660fe8f860e5c501648e17ab2b0e77472";

    @Override
    public void run() {
        Init.getLogger(getClass().getName()).info("HTTPDRunnable started");
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(Init.SERVER_PORT), 0);
            myCreateContext(httpServer, "/", "/echoHeader", "/echoGet", "/echoPost", "/favicon.ico");
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            Init.getLogger(getClass().getName()).warning(e.toString());
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

    private void myCreateContext(HttpServer server, String... path) {
        Stream.of(path).forEach((e) -> server.createContext(e, HandlerFactory.getHandler(e)));
    }

}
