package ru.chsergeyg.terrapia.server;

import ru.chsergeyg.terrapia.network.ServerSocketThread;
import ru.chsergeyg.terrapia.network.ServerSocketThreadEventHandler;
import ru.chsergeyg.terrapia.network.SocketThread;
import ru.chsergeyg.terrapia.network.SocketThreadEventHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class Core implements ServerSocketThreadEventHandler, SocketThreadEventHandler {

    private ServerSocketThread serverSocketThread;
    private String serverName = "TerraPia";

    void start(int port) {
        if (serverSocketThread == null || !serverSocketThread.isAlive())
            serverSocketThread = new ServerSocketThread(this, "Server Socket thread", port, 2000);
        else
            System.out.println("Server already started");
    }

    void stop() {
        if (serverSocketThread != null && serverSocketThread.isAlive())
            serverSocketThread.interrupt();
        else
            System.out.println("Nothing to stop");
    }

    public String status() {
        return String.format("Server %s runned\n", serverSocketThread.isAlive() ? "" : "not").trim();
    }

    public void putLog(String msg) {
        System.out.println(String.format("[%s] %s reports: %s",
                new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis()), Thread.currentThread().getName(), msg));
    }

    @Override
    public void onServerSocketThreadStarted(ServerSocketThread serverSocketThread) {
        putLog("Server socket thread started");
    }

    @Override
    public void onServerSocketThreadStopped(ServerSocketThread serverSocketThread) {
        putLog("Server socket thread stopped");
    }

    @Override
    public void onServerSocketException(ServerSocketThread serverSocketThread, Exception e) {
        putLog(String.format("Exception in server socket thread: %s. %s", serverSocketThread.getName(), e.getMessage()));
    }

    @Override
    public void onWaitingForConnect(ServerSocketThread serverSocketThread, ServerSocket serverSocket) {

    }

    @Override
    public void onSocketAccepted(ServerSocketThread serverSocketThread, ServerSocket serverSocket, Socket socket) {
        putLog("New socket accepted.");
        String name = "Socket_thread_" + socket.getInetAddress() + ":" + socket.getPort();
        new SocketThread(this, socket, name);
    }

    @Override
    public void onServerSocketTimeout(ServerSocketThread serverSocketThread, ServerSocket serverSocket) {

    }

    @Override
    public void onServerSocketCreated(ServerSocketThread serverSocketThread, ServerSocket serverSocket) {
        putLog("Server socket started");
    }


    @Override
    public synchronized void onSocketThreadStarted(SocketThread socketThread) {
        putLog("Socket thread '" + socketThread.getName() + "' started");
    }

    @Override
    public synchronized void onGetMessage(SocketThread socketThread, Socket socket, String msg) {
        String date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(System.currentTimeMillis());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("HTTP/1.1 %s\n\r", "200 OK"));
        stringBuilder.append(String.format("Date: %s\n\r", date));
        stringBuilder.append(String.format("Server: %s\n\r", serverName));
        stringBuilder.append(String.format("Last-Modified: %s\n\r", date));
        stringBuilder.append(String.format("Transfer-Encoding: %s\n\r", "deflate"));
        stringBuilder.append(String.format("Connection: %s\n\r", "Keep-Alive"));
        stringBuilder.append(String.format("Content-Type: %s; charset=%s\n\r\n\r", "text/html", "UTF-8"));
        stringBuilder.append(buildHTMLPage(msg));
        socketThread.sendMessage(stringBuilder.toString());
    }

    private String buildHTMLPage(String msg) {
        StringBuilder builder = new StringBuilder();
        String[] parts = msg.split("\n\r");
        builder.append(String.format("<html><head><title>%s</title></head><body>", serverName));
        for (String part : parts)
            builder.append(String.format("<p><code>%s</code></p>", part));
        builder.append("</body></html>");
        
        return builder.toString();
    }

    @Override
    public synchronized void onSocketIsReady(SocketThread socketThread, Socket socket) {
        putLog("Socket is ready");
    }

    @Override
    public synchronized void onSocketException(SocketThread socketThread, Exception e) {
        putLog(String.format("Exception in socket thread: %s. %s", socketThread.getName(), e.getMessage()));
    }

    @Override
    public synchronized void onSocketThreadStopped(SocketThread socketThread) {
        putLog("Socket Thread stooped");
    }
}
