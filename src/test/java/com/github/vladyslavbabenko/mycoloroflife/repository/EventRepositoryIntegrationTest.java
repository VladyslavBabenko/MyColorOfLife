package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DisplayName("Integration-level testing for EventRepository")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EventRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    private final User testAdmin = User.builder().id(2).username("TestAdmin").email("TestAdmin@mail.com").build(),
            testAuthor = User.builder().id(3).username("TestAuthor").email("TestAuthor@mail.com").build();
    private Event expectedFirstEvent, expectedSecondEvent;
    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        //given
        expectedFirstEvent = Event.builder()
                .id(1)
                .dateTimeOfCreation("2022.08.05 22:00")
                .title("First test title")
                .text("First test text. First test text. First test text.")
                .users(Collections.singleton(testAdmin))
                .build();

        expectedSecondEvent = Event.builder()
                .id(2)
                .dateTimeOfCreation("2022.08.05 22:05")
                .title("Second test title")
                .text("Second test text. Second test text. Second test text.")
                .users(Collections.singleton(testAuthor))
                .build();
    }

    @Test
    void shouldFindEventByTitle() {
        Optional<Event> actualEvent = eventRepository.findEventByTitle(expectedFirstEvent.getTitle());

        Assertions.assertThat(actualEvent.get().getId()).isEqualTo(expectedFirstEvent.getId());
        Assertions.assertThat(actualEvent.get().getDateTimeOfCreation()).isEqualTo(expectedFirstEvent.getDateTimeOfCreation());
        Assertions.assertThat(actualEvent.get().getTitle()).isEqualTo(expectedFirstEvent.getTitle());
        Assertions.assertThat(actualEvent.get().getText()).isEqualTo(expectedFirstEvent.getText());
        Assertions.assertThat(actualEvent.get().getUsers().stream().findFirst().get().getUsername())
                .isEqualTo(expectedFirstEvent.getUsers().stream().findFirst().get().getUsername());
    }

    @Test
    void shouldFindOneEventByKeywordInTitle() {
        Optional<List<Event>> actualEvent = eventRepository.findByTitleContains("First");

        Assertions.assertThat(actualEvent.get()).hasSize(1);

        Assertions.assertThat(actualEvent.get().get(0).getId()).isEqualTo(expectedFirstEvent.getId());
        Assertions.assertThat(actualEvent.get().get(0).getDateTimeOfCreation()).isEqualTo(expectedFirstEvent.getDateTimeOfCreation());
        Assertions.assertThat(actualEvent.get().get(0).getTitle()).isEqualTo(expectedFirstEvent.getTitle());
        Assertions.assertThat(actualEvent.get().get(0).getText()).isEqualTo(expectedFirstEvent.getText());
        Assertions.assertThat(actualEvent.get().get(0).getUsers().stream().findFirst().get().getUsername())
                .isEqualTo(expectedFirstEvent.getUsers().stream().findFirst().get().getUsername());
    }

    @Test
    void shouldFindTwoEventsByKeywordInTitle() {
        Optional<List<Event>> actualEvent = eventRepository.findByTitleContains("test");

        Assertions.assertThat(actualEvent.get()).hasSize(2);

        Assertions.assertThat(actualEvent.get().get(0).getId()).isEqualTo(expectedFirstEvent.getId());
        Assertions.assertThat(actualEvent.get().get(0).getDateTimeOfCreation()).isEqualTo(expectedFirstEvent.getDateTimeOfCreation());
        Assertions.assertThat(actualEvent.get().get(0).getTitle()).isEqualTo(expectedFirstEvent.getTitle());
        Assertions.assertThat(actualEvent.get().get(0).getText()).isEqualTo(expectedFirstEvent.getText());
        Assertions.assertThat(actualEvent.get().get(0).getUsers().stream().findFirst().get().getUsername())
                .isEqualTo(expectedFirstEvent.getUsers().stream().findFirst().get().getUsername());

        Assertions.assertThat(actualEvent.get().get(1).getId()).isEqualTo(expectedSecondEvent.getId());
        Assertions.assertThat(actualEvent.get().get(1).getDateTimeOfCreation()).isEqualTo(expectedSecondEvent.getDateTimeOfCreation());
        Assertions.assertThat(actualEvent.get().get(1).getTitle()).isEqualTo(expectedSecondEvent.getTitle());
        Assertions.assertThat(actualEvent.get().get(1).getText()).isEqualTo(expectedSecondEvent.getText());
        Assertions.assertThat(actualEvent.get().get(1).getUsers().stream().findFirst().get().getUsername())
                .isEqualTo(expectedSecondEvent.getUsers().stream().findFirst().get().getUsername());
    }
}