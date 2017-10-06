package ru.chsergeyg.terrapia.network;

import ru.chsergeyg.terrapia.server.MyGpio;

public class ServerSocketThread extends Thread {

    private final int port;

    public ServerSocketThread(int port, String name) {
        super(name);
        this.port = port;
        start();
    }

    @Override
    public void run() {
        System.out.println("SSK started");
        while (!isInterrupted()) {
            try {
                MyGpio.go();
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("SSK stopped");
    }
}
