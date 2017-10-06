package ru.chsergeyg.terrapia.server;

import ru.chsergeyg.terrapia.network.ServerSocketThread;

public class Core {

    private static ServerSocketThread serverSocket;

    void start(int port) {
        if (serverSocket == null || !serverSocket.isAlive())
            serverSocket = new ServerSocketThread(port, "Server Socket thread");
        else
            System.out.println("Server already started");
    }

    void stop() {
        if (serverSocket != null && serverSocket.isAlive())
            serverSocket.interrupt();
         else
            System.out.println("Nothing to stop");
    }

    public String status() {
        return String.format("Server %s runned\n", serverSocket.isAlive()?"":"not").trim();
    }
}
