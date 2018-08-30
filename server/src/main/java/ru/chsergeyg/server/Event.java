package ru.chsergeyg.server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    public String what;
    public LocalDateTime when;
    public String action;

    Event(String what, String action) {
        this.what = what;
        this.action = action;
        when = LocalDateTime.now();
    }

}
