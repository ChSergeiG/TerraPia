package ru.chsergeyg.terrapia.network;

import java.io.*;
import java.net.Socket;

public class SocketThread extends Thread {

    SocketThreadEventHandler eventHandler;
    Socket socket;

    BufferedWriter out;

    public SocketThread(SocketThreadEventHandler eventHandler, Socket socket, String name) {
        super(name);
        this.eventHandler = eventHandler;
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        eventHandler.onSocketThreadStarted(this);
        try {
            eventHandler.onSocketIsReady(this, socket);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            StringBuilder bld = new StringBuilder();



            bld.append("Got connection from ");
            bld.append(socket.getInetAddress().getHostName());
            bld.append("\n\r");
            while (in.ready()) {
                bld.append(in.readLine());
                bld.append("\n\r");
            }
            eventHandler.onGetMessage(this, socket, bld.toString());
        } catch (IOException e) {
            eventHandler.onSocketException(this, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                eventHandler.onSocketException(this, e);
            }
        }
        eventHandler.onSocketThreadStopped(this);
    }

    public synchronized boolean sendMessage(String msg) {
        try {
            out.write(msg);
            out.flush();
            return true;
        } catch (IOException e) {
            eventHandler.onSocketException(this, e);
            stopThisThread();
            return false;
        }
    }

    public synchronized void stopThisThread() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventHandler.onSocketException(this, e);
        }
    }
}
