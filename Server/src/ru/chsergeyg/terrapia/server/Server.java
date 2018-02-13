package ru.chsergeyg.terrapia.server;

import fi.iki.elonen.util.ServerRunner;

public class Server {


    public static void main(String[] args) {
        new SerialThread().start();
        ServerRunner.run(HTTPDThread.class);
    }
}

