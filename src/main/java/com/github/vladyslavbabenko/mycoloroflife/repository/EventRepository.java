package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Repository} for handling with {@link Event} entity.
 */

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    /**
     * Finds an {@link Event} by title.
     *
     * @param title title to search
     * @return Optional Event from database, otherwise empty Optional
     */
    Optional<Event> findEventByTitle(String title);

    /**
     * Finds all {@link Event} with keyword in title.
     *
     * @param keyword keyword to search
     * @return List of Events from database, otherwise empty List
     */
    Optional<List<Event>> findByTitleContains(String keyword);

    /**
     * Finds an {@link Event} by title.
     *
     * @param title title to search
     * @return true if exists, otherwise false
     */
    boolean existsByTitle(String title);
}