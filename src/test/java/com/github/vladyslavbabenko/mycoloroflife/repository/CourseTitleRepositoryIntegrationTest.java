package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@DisplayName("Integration-level testing for CourseTitleRepository")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CourseTitleRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Autowired
    private CourseTitleRepository courseTitleRepository;
    private CourseTitle testCourseTitle;

    @BeforeEach
    void setUp() {
        //given
        testCourseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();
    }


    @Test
    void findByTitle() {
        Optional<CourseTitle> courseTitleFromDB = courseTitleRepository.findByTitle(testCourseTitle.getTitle());

        Assertions.assertThat(courseTitleFromDB.get().getId()).isEqualTo(testCourseTitle.getId());
        Assertions.assertThat(courseTitleFromDB.get().getTitle()).isEqualTo(testCourseTitle.getTitle());
    }

    @Test
    void findAllByTitleContains() {
        Optional<List<CourseTitle>> courseTitlesFromDB = courseTitleRepository.findAllByTitleContains(testCourseTitle.getTitle());

        Assertions.assertThat(courseTitlesFromDB.get().size()).isEqualTo(2);
    }

    @Test
    void findById() {
        Optional<CourseTitle> courseTitleFromDB = courseTitleRepository.findById(testCourseTitle.getId());

        Assertions.assertThat(courseTitleFromDB.get().getId()).isEqualTo(testCourseTitle.getId());
        Assertions.assertThat(courseTitleFromDB.get().getTitle()).isEqualTo(testCourseTitle.getTitle());
    }

    @Test
    void existsByTitle() {
        boolean existsByTitle = courseTitleRepository.existsByTitle(testCourseTitle.getTitle());

        Assertions.assertThat(existsByTitle).isTrue();
    }
}