package ru.chsergeyg.terrapia.server;

import com.sun.net.httpserver.HttpHandler;

public enum HandlerEnum {
    ROOT("/", "www/index.html", new HandlerStorage.RootHandler()),
    FAVICON("/favicon.ico", "www/favicon.ico", new HandlerStorage.FaviconHandler()),
    SHA256("/js/sha256.js", "www/js/sha256.js", new HandlerStorage.SHAHandler()),
    TERR("/terr", null, new HandlerStorage.TerrHandler()),
    CSS("/css/style.css", "www/css/style.css", new HandlerStorage.CssHandler()),
    ECHO_POST("/echoPost", null, new HandlerStorage.EchoPostHandler());

    private String url;

    public String getFilePath() {
        return filePath;
    }

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
}
