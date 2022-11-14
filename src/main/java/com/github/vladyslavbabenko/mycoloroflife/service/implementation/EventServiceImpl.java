package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import com.github.vladyslavbabenko.mycoloroflife.repository.EventRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link EventService}.
 */

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        events.sort(Comparator.comparingLong(Event::getId).reversed());
        return events;
    }

    @Override
    public List<Event> findByKeyword(String keyword) {
        return eventRepository.findByTitleContains(keyword).orElse(Collections.emptyList());
    }

    @Override
    public Event findById(Integer eventId) {
        return eventRepository.findById(eventId).orElse(new Event());
    }

    @Override
    public Optional<Event> optionalFindById(Integer eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    public boolean saveEvent(Event eventToSave) {
        if (eventRepository.existsByTitle(eventToSave.getTitle())) {
            return false;
        }

        eventRepository.save(eventToSave);

        log.info("Event with title {} saved successfully", eventToSave.getTitle());

        return true;
    }

    @Override
    public boolean deleteEvent(Integer eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);

            log.info("Event with id {} deleted successfully", eventId);

            return true;
        }

        return false;
    }

    @Override
    public boolean updateEvent(Event updatedEvent) {
        Optional<Event> optionalEvent = eventRepository.findById(updatedEvent.getId());

        if (optionalEvent.isEmpty()) {
            return false;
        }

        Event EventToUpdate = optionalEvent.get();

        EventToUpdate.setTitle(updatedEvent.getTitle());
        EventToUpdate.setText(updatedEvent.getText());
        EventToUpdate.setDateTimeOfCreation(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

        eventRepository.save(EventToUpdate);

        log.info("Event with title {} updated successfully", optionalEvent.get().getTitle());

        return true;
    }
}