package ru.chsergeyg.terrapia.server;

import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) {
        Init.init();
        Thread thSerial = new Thread(null, new SerialRunnable(), "Serial thread");
        Thread thHTTPD = new Thread(null, new HTTPDRunnable(), "HTTPD thread");
        Thread thPi = new Thread(null, new PiRunnable(), "Pi4j thread");
        try {
            thSerial.join();
            thHTTPD.join();
            thPi.join();
        } catch (InterruptedException e) {
            Init.getLogger(Server.class.getName()).warning(e.toString());
        }
        thSerial.start();
        thHTTPD.start();
        thPi.start();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Init.getLogger(Server.class.getName()).warning(e.toString());
        }
        thHTTPD.interrupt();
        thSerial.interrupt();
        thPi.interrupt();
    }
}

