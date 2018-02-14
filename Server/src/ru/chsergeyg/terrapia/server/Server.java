package ru.chsergeyg.terrapia.server;

import ru.chsergeyg.terrapia.server.runnable.HTTPDRunnable;
import ru.chsergeyg.terrapia.server.runnable.PiRunnable;
import ru.chsergeyg.terrapia.server.runnable.SerialRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) {
        Init.init();
        Collection<Thread> threads = new ArrayList<>();
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
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Init.getLogger(Server.class.getName()).warning(e.toString());
        }
        threads.forEach(Thread::interrupt);
    }
}

