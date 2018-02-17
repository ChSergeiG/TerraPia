package ru.chsergeyg.terrapia.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.chsergeyg.terrapia.server.runnable.HTTPDRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum HandlerEnum {
    ROOT("/", "www/index.html", new RootHandler()),
    FAVICON("/favicon.ico", "www/favicon.ico", new FaviconHandler()),
    SHA256("/js/sha256.js", "www/js/sha256.js", new SHAHandler()),
    TERR("/terr", "www/terr.html", new TerrHandler()),

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
            Map<String, Object> parameters = new HashMap<>();
            String query = exchange.getRequestURI().getRawQuery();
            parseQuery(query, parameters);
            StringBuilder response = new StringBuilder();
            if (HTTPDRunnable.isUserValid((String) parameters.get("user"), (String) parameters.get("password")))
                parameters.forEach((k, v) -> response.append(k).append(" -> ").append(v).append("\n"));
            else
                response.append("<a href=\"..\">Back</a>");

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

    public static class SHAHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get(SHA256.filePath));
            processResponse(response, exchange, getClass());
        }
    }

    public static class EchoPostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> parameters = new HashMap<>();
            String query = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "utf-8")).readLine();
            parseQuery(query, parameters);
            StringBuilder response = new StringBuilder();
            for (String key : parameters.keySet()) {
                response.append(key);
                response.append(" = ");
                response.append(parameters.get(key));
                response.append("\n");
            }
            processResponse(response, exchange, getClass());
        }
    }

    private static void processResponse(StringBuilder response, HttpExchange exchange, Class handler) throws IOException {
        processResponse(response.toString().getBytes(), exchange, handler);
    }

    private static void processResponse(String response, HttpExchange exchange, Class handler) throws IOException {
        processResponse(response.getBytes(), exchange, handler);
    }

    private static void processResponse(byte[] response, HttpExchange exchange, Class handler) throws IOException {
        Init.getLogger(handler.getEnclosingClass().getName()).info(handler + ": size: " + response.length + " bytes");
        exchange.sendResponseHeaders(200, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
                }
                if (param.length > 1) {
                    value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
                }
                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }

}
