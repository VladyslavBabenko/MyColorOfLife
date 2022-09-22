package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseProgress;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@DisplayName("Integration-level testing for CourseProgressRepository")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CourseProgressRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    private CourseProgress expectedCourseProgress;
    private User expectedUser;
    private Course expectedCourse;
    private CourseTitle expectedCourseTitle;

    @Autowired
    private CourseProgressRepository courseProgressRepository;

    @BeforeEach
    void setUp() {
        //given
        expectedUser = User.builder()
                .id(1)
                .username("TestUser")
                .email("TestUser@mail.com")
                .build();

        expectedCourseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();

        expectedCourse = Course.builder()
                .id(1)
                .courseTitle(expectedCourseTitle)
                .page(1)
                .text("Test Text 1")
                .build();

        expectedCourseProgress = CourseProgress.builder().id(1).user(expectedUser).course(expectedCourse).build();
    }

    @Test
    void findAllByUser() {
        //when
        Optional<List<CourseProgress>> courseProgressList = courseProgressRepository.findAllByUser(expectedUser);

        //then
        Assertions.assertThat(courseProgressList.get()).hasSize(2);
    }

    @Test
    void findByUserAndCourse() {
        //when
        Optional<CourseProgress> actualCourseProgress = courseProgressRepository.findByUserAndCourse(expectedUser, expectedCourse);

        //then
        Assertions.assertThat(actualCourseProgress.get().getId()).isEqualTo(expectedCourseProgress.getId());
        Assertions.assertThat(actualCourseProgress.get().getCourse().getCourseTitle().getTitle()).isEqualTo(expectedCourseProgress.getCourse().getCourseTitle().getTitle());
        Assertions.assertThat(actualCourseProgress.get().getCourse().getPage()).isEqualTo(expectedCourseProgress.getCourse().getPage());
        Assertions.assertThat(actualCourseProgress.get().getUser().getUsername()).isEqualTo(expectedCourseProgress.getUser().getUsername());
    }

    @Test
    void existsByUserAndCourse() {
        //when
        boolean exists = courseProgressRepository.existsByUserAndCourse(expectedUser, expectedCourse);

        //then
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void existsByUser() {
        //when
        boolean exists = courseProgressRepository.existsByUser(expectedUser);

        //then
        Assertions.assertThat(exists).isTrue();
    }
}