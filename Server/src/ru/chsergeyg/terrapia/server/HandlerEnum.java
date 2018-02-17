package ru.chsergeyg.terrapia.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum HandlerEnum {
    ROOT("/", "www/index.html", new RootHandler()),
    FAVICON("/favicon.ico", "www/favicon.ico", new FaviconHandler()),
    TERR("/terr", "www/terr.html", new TerrHandler()),
    ECHO_HEADER("/echoHeader", null, new EchoHeaderHandler()),
    ECHO_GET("/echoGet", null, new EchoGetHandler()),
    ECHO_POST("/echoPost", null, new EchoPostHandler());

    private String url;
    private String filePath;
    private HttpHandler handler;

    HandlerEnum(String url, String filePath, HttpHandler handler) {
        this.url = url;
        this.filePath = filePath;
        this.handler = handler;
    }

    public String getUrl() {
        return url;
    }

    public HttpHandler getHandler() {
        return handler;
    }

    public static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get(ROOT.filePath));
            processResponse(response, exchange, getClass());
        }
    }

    public static class TerrHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get(TERR.filePath));
            processResponse(response, exchange, getClass());
        }
    }

    public static class FaviconHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get(FAVICON.filePath));
            processResponse(response, exchange, getClass());
        }
    }

    public static class EchoHeaderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getRequestHeaders();
            Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
            StringBuilder reply = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : entries) {
                reply.append(entry.toString());
                reply.append("\n");
            }
            byte[] response = reply.toString().getBytes();

            processResponse(response, exchange, getClass());
        }
    }

    public static class EchoGetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = ("<h1>Server start success if you see this message</h1>" + "<h1>Port: " + Init.HTTPD_PORT + "</h1>").getBytes();
            processResponse(response, exchange, getClass());
        }
    }

    public static class EchoPostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = ("<h1>Server start success if you see this message</h1>" + "<h1>Port: " + Init.HTTPD_PORT + "</h1>").getBytes();
            processResponse(response, exchange, getClass());
        }
    }

    private static void processResponse(byte[] response, HttpExchange exchange, Class handler) throws IOException {
        Init.getLogger(handler.getEnclosingClass().getName()).info(handler + ": size: " + response.length + " bytes");
        exchange.sendResponseHeaders(200, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();

    }

}
