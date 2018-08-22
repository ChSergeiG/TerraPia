package ru.chsergeyg.terrapia.server;

import com.sun.net.httpserver.HttpHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum HandlerEnum {
    ROOT("/", new HandlerStorage.RootHandler()),
    FAVICON("/favicon.ico", new HandlerStorage.FaviconHandler(), "www", "favicon.ico"),
    TERR("/terr", new HandlerStorage.TerrHandler()),
    CSS("/css/style.css", new HandlerStorage.CssHandler(), "www", "css", "style.css");

    private Path filePath;
    private HttpHandler handler;
    private String url;

    HandlerEnum(String url, HttpHandler handler, String... filePath) {
        this.url = url;
        this.filePath = Paths.get(Init.WORKING_PATH.toString(), filePath);
        this.handler = handler;
    }

    public String getUrl() {
        return url;
    }

    public Path getFilePath() {
        return filePath;
    }

    public HttpHandler getHandler() {
        return handler;
    }
}
