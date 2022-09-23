package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.repository.EventRepository;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

@DisplayName("Unit-level testing for EventService")
class EventServiceImplTest {

    private User testAuthor;
    private EventService eventService;
    private EventRepository eventRepository;
    private Event firstTestEvent;

    @BeforeEach
    void setUp() {
        //given
        eventRepository = Mockito.mock(EventRepository.class);
        eventService = new EventServiceImpl(eventRepository);
        testAuthor = User.builder()
                .id(3)
                .username("TestAuthor")
                .email("TestAuthor@mail.com")
                .build();

        firstTestEvent = Event.builder()
                .id(1)
                .dateTimeOfCreation("2022.08.05 22:00")
                .title("First test title")
                .text("First test text. First test text. First test text.")
                .users(Collections.singleton(testAuthor))
                .build();
    }

    @Test
    void isEventServiceImplTestReady() {
        Assertions.assertThat(eventRepository).isNotNull().isInstanceOf(EventRepository.class);
        Assertions.assertThat(eventService).isNotNull().isInstanceOf(EventService.class);
        Assertions.assertThat(testAuthor).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(firstTestEvent).isNotNull().isInstanceOf(Event.class);
    }

    @Test
    void shouldGetAllEvents() {
        //when
        eventService.getAllEvents();

        Mockito.verify(eventRepository, Mockito.times(1)).findAll();
    }

    @Test
    void shouldFindByKeyword() {
        String keyword = "First";

        //when
        eventService.findByKeyword(keyword);

        Mockito.doReturn(Optional.of(firstTestEvent))
                .when(eventRepository)
                .findByTitleContains(keyword);

        Mockito.verify(eventRepository, Mockito.times(1)).findByTitleContains(keyword);
    }

    @Test
    void shouldFIndEventById() {
        //when
        Mockito.doReturn(Optional.of(firstTestEvent))
                .when(eventRepository)
                .findById(firstTestEvent.getId());

        eventService.findById(firstTestEvent.getId());

        //then
        Mockito.verify(eventRepository, Mockito.times(1)).findById(firstTestEvent.getId());
    }

    @Test
    void shouldSaveEvent() {
        //when
        Mockito.doReturn(false)
                .when(eventRepository)
                .existsByTitle(firstTestEvent.getTitle());

        boolean isSaved = eventService.saveEvent(firstTestEvent);

        //then
        Mockito.verify(eventRepository, Mockito.times(1)).save(firstTestEvent);
        Assertions.assertThat(isSaved).isTrue();
    }

    @Test
    void shouldNotSaveEvent() {
        //when
        Mockito.doReturn(true)
                .when(eventRepository)
                .existsByTitle(firstTestEvent.getTitle());

        boolean isSaved = eventService.saveEvent(firstTestEvent);

        //then
        Mockito.verify(eventRepository, Mockito.times(0)).save(firstTestEvent);
        Assertions.assertThat(isSaved).isFalse();
    }

    @Test
    void shouldNotDeleteEvent() {
        //when
        boolean isEventDeleted = eventService.deleteEvent(firstTestEvent.getId());

        //then
        Mockito.verify(eventRepository, Mockito.times(1)).existsById(firstTestEvent.getId());
        Mockito.verify(eventRepository, Mockito.times(0)).deleteById(firstTestEvent.getId());
        Assertions.assertThat(isEventDeleted).isFalse();
    }

    @Test
    void shouldDeleteEvent() {
        //when
        Mockito.doReturn(true).when(eventRepository).existsById(firstTestEvent.getId());
        boolean isEventDeleted = eventService.deleteEvent(firstTestEvent.getId());

        //then
        Mockito.verify(eventRepository, Mockito.times(1)).existsById(firstTestEvent.getId());
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(firstTestEvent.getId());
        Assertions.assertThat(isEventDeleted).isTrue();
    }

    @Test
    void shouldUpdateEvent() {
        //when
        Mockito.doReturn(Optional.of(firstTestEvent)).when(eventRepository).findById(firstTestEvent.getId());
        boolean isEventUpdated = eventService.updateEvent(firstTestEvent);

        //then
        Mockito.verify(eventRepository, Mockito.times(1)).save(firstTestEvent);
        Assertions.assertThat(isEventUpdated).isTrue();
    }

    @Test
    void shouldNotUpdateEvent() {
        //when
        Mockito.doReturn(Optional.empty()).when(eventRepository).findById(firstTestEvent.getId());
        boolean isEventUpdated = eventService.updateEvent(firstTestEvent);

        //then
        Mockito.verify(eventRepository, Mockito.times(0)).save(firstTestEvent);
        Assertions.assertThat(isEventUpdated).isFalse();
    }
}