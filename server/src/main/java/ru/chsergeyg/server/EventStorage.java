package ru.chsergeyg.server;

import org.springframework.data.repository.CrudRepository;

interface EventStorage extends CrudRepository<Event, Long> {
}
