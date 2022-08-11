package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import com.github.vladyslavbabenko.mycoloroflife.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public boolean saveEvent(Event eventToSave) {
        if (eventRepository.existsByTitle(eventToSave.getTitle())) {
            return false;
        } else {
            eventRepository.save(eventToSave);
            return true;
        }
    }

    @Override
    public boolean deleteEvent(Integer eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
            return true;
        } else return false;
    }

    @Override
    public boolean updateEvent(Event updatedEvent) {
        Optional<Event> optionalEvent = eventRepository.findById(updatedEvent.getId());

        if (optionalEvent.isEmpty()) {
            return false;
        } else {
            Event EventToUpdate = optionalEvent.get();

            EventToUpdate.setTitle(updatedEvent.getTitle());
            EventToUpdate.setText(updatedEvent.getText());
            EventToUpdate.setDateTimeOfCreation(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

            eventRepository.save(EventToUpdate);
            return true;
        }
    }
}