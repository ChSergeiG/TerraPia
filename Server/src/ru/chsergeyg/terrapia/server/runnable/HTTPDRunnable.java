package ru.chsergeyg.terrapia.server.runnable;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.chsergeyg.terrapia.server.Init;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HTTPDRunnable implements Runnable {

    private static final String LOGIN = "root";
    private static final String PASS_HASH = "982f249c728f730cbe5ddd7b02a3c3e660fe8f860e5c501648e17ab2b0e77472";

    @Override
    public void run() {
        Init.getLogger(getClass().getName()).info("HTTPDRunnable started");
        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            Init.getLogger(getClass().getName()).warning(e.toString());
        }
        try {
            httpServer.createContext("/", (e) -> handlePage(e, "www/index.html"));
            httpServer.createContext("/terr", (e) -> handlePage(e, "www/terr.html"));
        } catch (NullPointerException e) {
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

    private void handlePage(HttpExchange httpExchange, String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        httpExchange.sendResponseHeaders(200, bytes.length);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(bytes);
        responseBody.close();
    }

}
