package ru.chsergeyg.terrapia.network;

import java.net.ServerSocket;
import java.net.Socket;

public interface ServerSocketThreadEventHandler {
    void onServerSocketThreadStarted(ServerSocketThread serverSocketThread);

    void onServerSocketThreadStopped(ServerSocketThread serverSocketThread);

    void onServerSocketException(ServerSocketThread serverSocketThread, Exception e);

    void onWaitingForConnect(ServerSocketThread serverSocketThread, ServerSocket serverSocket);

    void onSocketAccepted(ServerSocketThread serverSocketThread, ServerSocket serverSocket, Socket socket);

    void onServerSocketTimeout(ServerSocketThread serverSocketThread, ServerSocket serverSocket);

    void onServerSocketCreated(ServerSocketThread serverSocketThread, ServerSocket serverSocket);
}
