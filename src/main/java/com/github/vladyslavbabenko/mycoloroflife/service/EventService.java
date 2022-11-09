package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling with {@link Event} entity.
 */

public interface EventService {
    /**
     * Get all {@link Event} entities.
     *
     * @return the collection of {@link Event} objects.
     */
    List<Event> getAllEvents();

    /**
     * Finds all {@link Event} with keyword in title or text.
     *
     * @param keyword keyword to search
     * @return List of Events from database, empty List
     */
    List<Event> findByKeyword(String keyword);

    /**
     * Find {@link Event} by eventId.
     *
     * @param eventId provided {@link Event} id.
     * @return {@link Event} with the provided id, or new {@link Event} otherwise.
     */
    Event findById(Integer eventId);

    /**
     * Find {@link Event} by eventId.
     *
     * @param eventId provided {@link Event} id.
     * @return Optional {@link Event} with the provided id, or empty Optional otherwise.
     */
    Optional<Event> optionalFindById(Integer eventId);


    /**
     * Save provided {@link Event} entity.
     *
     * @param eventToSave provided {@link Event} to save.
     * @return true if operation was successful, otherwise false.
     */
    boolean saveEvent(Event eventToSave);

    /**
     * Delete provided {@link Event} entity.
     *
     * @param eventId id by which the Event will be deleted, if present
     * @return true if operation was successful, otherwise false.
     */
    boolean deleteEvent(Integer eventId);

    /**
     * Update provided {@link Event} entity.
     *
     * @param updatedEvent {@link Event} with updated fields that will replace the old one.
     * @return true if operation was successful, otherwise false.
     */
    boolean updateEvent(Event updatedEvent);
}
