package ru.chsergeyg.terrapia.server.runnable;

import ru.chsergeyg.terrapia.server.Init;

public class PiRunnable implements Runnable {

    @Override
    public void run() {
        Init.getLogger(getClass().getName()).info("PiRunnable started");
        Init.getLogger(getClass().getName()).info("PiRunnable stopped");
    }
}
