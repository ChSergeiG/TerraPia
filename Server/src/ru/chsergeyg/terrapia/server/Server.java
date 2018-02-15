package ru.chsergeyg.terrapia.server;

import ru.chsergeyg.terrapia.server.runnable.HTTPDRunnable;
import ru.chsergeyg.terrapia.server.runnable.PiRunnable;
import ru.chsergeyg.terrapia.server.runnable.SerialRunnable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class Server {
    private static Collection<Thread> threads;

    public static void main(String[] args) {
        Init.init();
        threads = new ArrayList<>();
        threads.add(new Thread(null, new SerialRunnable(), "Serial thread"));
        threads.add(new Thread(null, new HTTPDRunnable(), "HTTPD thread"));
        threads.add(new Thread(null, new PiRunnable(), "Pi4j thread"));
        threads.forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException exc) {
                Init.getLogger(Server.class.getName()).warning(exc.toString());
            }
        });
        threads.forEach(Thread::start);

        BufferedReader brd = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (!brd.readLine().equals("quit")) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            Init.getLogger(Server.class.getName()).warning(e.toString());
        }
        threads.forEach(Thread::interrupt);
    }
}

