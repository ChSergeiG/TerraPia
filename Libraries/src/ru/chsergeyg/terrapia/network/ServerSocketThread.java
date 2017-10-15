package ru.chsergeyg.terrapia.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {

    ServerSocketThreadEventHandler eventHandler;
    int port;
    int timeout;

    public ServerSocketThread(ServerSocketThreadEventHandler eventHandler, String name, int port, int timeout) {
        super(name);
        this.eventHandler = eventHandler;
        this.port = port;
        this.timeout = timeout;
        start();
    }

    @Override
    public void run() {
        eventHandler.onServerSocketThreadStarted(this);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            eventHandler.onServerSocketCreated(this, serverSocket);
            serverSocket.setSoTimeout(timeout);
            while (!isInterrupted()) {
                Socket socket;
                try {
                    eventHandler.onWaitingForConnect(this, serverSocket);
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    eventHandler.onServerSocketTimeout(this, serverSocket);
                    continue;
                }
                eventHandler.onSocketAccepted(this, serverSocket, socket);
            }
        } catch (IOException e) {
            eventHandler.onServerSocketException(this, e);
        } finally {
            eventHandler.onServerSocketThreadStopped(this);
        }
    }
}
