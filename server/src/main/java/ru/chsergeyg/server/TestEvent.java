package ru.chsergeyg.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestEvent implements CommandLineRunner {

    private final EventStorage repository;

    @Autowired
    public TestEvent(EventStorage repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        this.repository.save(new Event("Me", "Stay"));
    }
}