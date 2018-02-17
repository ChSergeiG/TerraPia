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

public class HandlerFactory {

    public static HttpHandler getHandler(String path) {
        switch (path) {
            case "/":
                return new RootHandler();
            case "/terr":
                return new TerrHandler();
            case "/echoHeader":
                return new EchoHeaderHandler();
            case "/echoGet":
                return new EchoGetHandler();
            case "/echoPost":
                return new EchoPostHandler();
            case "/favicon.ico":
                return new EchoFaviconHandler();
            default:
                return new Handler404();
        }
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Init.getLogger(getClass().getName()).info("/ handled");
            StringBuilder response = new StringBuilder();
            response.append("<h1>Server running on port ");
            response.append(Init.SERVER_PORT);
            response.append("</h1>");
            exchange.sendResponseHeaders(200, response.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }

    static class TerrHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Init.getLogger(getClass().getName()).info("/terr handled");
            handlePage(exchange, "www/terr.html");
        }
    }

    static class EchoHeaderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Init.getLogger(getClass().getName()).info("/echoHeader handled");
            Headers headers = exchange.getRequestHeaders();
            Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
            StringBuilder response = new StringBuilder();
            entries.forEach((entry) -> response.append(entry.toString() + "\n"));
            exchange.sendResponseHeaders(200, response.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }

    static class EchoGetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Init.getLogger(getClass().getName()).info("/echoGet handled");
            exchange.sendResponseHeaders(200, "".getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(new byte[0]);
            os.close();
        }
    }

    static class EchoPostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Init.getLogger(getClass().getName()).info("/echoPost handled");
            exchange.sendResponseHeaders(200, "".getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(new byte[0]);
            os.close();
        }
    }

    static class EchoFaviconHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Init.getLogger(getClass().getName()).info("/favicon.ico handled");
            handlePage(exchange, "www/favicon.ico");
        }
    }

    static class Handler404 implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }


    static void handlePage(com.sun.net.httpserver.HttpExchange httpExchange, String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        httpExchange.sendResponseHeaders(200, bytes.length);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(bytes);
        responseBody.close();
    }

}
