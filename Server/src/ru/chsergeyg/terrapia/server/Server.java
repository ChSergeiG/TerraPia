package ru.chsergeyg.terrapia.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) {
        Init init = new Init();
        BufferedReader brd = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (!brd.readLine().equals("quit")) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            Init.getLogger(Server.class.getName()).warning(e.toString());
        }
        init.threads.forEach(Thread::interrupt);
    }
}

