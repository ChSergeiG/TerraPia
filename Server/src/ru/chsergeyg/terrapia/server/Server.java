package ru.chsergeyg.terrapia.server;

import fi.iki.elonen.util.ServerRunner;

public class Server {


    public static void main(String[] args) {
        ServerRunner.run(HTTPDThread.class);
    }
}

