package ru.chsergeyg.terrapia.server;

public class PiRunnable implements Runnable {

    @Override
    public void run() {
        Init.getLogger(PiRunnable.class.getName()).info("PI thread started");
    }
}
