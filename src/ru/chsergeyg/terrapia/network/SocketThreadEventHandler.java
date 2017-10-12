package ru.chsergeyg.terrapia.network;

import java.net.Socket;

public interface SocketThreadEventHandler {
    void onSocketThreadStarted(SocketThread socketThread);

    void onGetMessage(SocketThread socketThread, Socket socket, String msg);

    void onSocketIsReady(SocketThread socketThread, Socket socket);

    void onSocketException(SocketThread socketThread, Exception e);

    void onSocketThreadStopped(SocketThread socketThread);
}
