package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@DisplayName("Integration-level testing for CourseRepository")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CourseRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    private Course expectedFirstCourse, expectedSecondCourse;
    private CourseTitle courseTitle;
    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        //given
        courseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();

        expectedFirstCourse = Course.builder()
                .id(1)
                .courseTitle(courseTitle)
                .page(1)
                .text("Test Text 1")
                .build();

        expectedSecondCourse = Course.builder()
                .id(2)
                .courseTitle(courseTitle)
                .page(2)
                .videoTitle("Test Video Title 2")
                .videoLink("Test Video Link 2")
                .text("Test Text 2")
                .build();
    }

    @Test
    void shouldFindByCourseTitleAndPage() {
        Optional<Course> actualCourse = courseRepository.findByCourseTitleAndPage(expectedFirstCourse.getCourseTitle(), expectedFirstCourse.getPage());

        Assertions.assertThat(actualCourse.get().getId()).isEqualTo(expectedFirstCourse.getId());
        Assertions.assertThat(actualCourse.get().getCourseTitle().getTitle()).isEqualTo(expectedFirstCourse.getCourseTitle().getTitle());
        Assertions.assertThat(actualCourse.get().getVideoTitle()).isEqualTo(expectedFirstCourse.getVideoTitle());
        Assertions.assertThat(actualCourse.get().getVideoLink()).isEqualTo(expectedFirstCourse.getVideoLink());
        Assertions.assertThat(actualCourse.get().getText()).isEqualTo(expectedFirstCourse.getText());
        Assertions.assertThat(actualCourse.get().getPage()).isEqualTo(expectedFirstCourse.getPage());
    }

    @Test
    void shouldFindByVideoTitle() {
        Optional<Course> actualCourse = courseRepository.findByVideoTitle(expectedSecondCourse.getVideoTitle());

        Assertions.assertThat(actualCourse.get().getId()).isEqualTo(expectedSecondCourse.getId());
        Assertions.assertThat(actualCourse.get().getCourseTitle().getTitle()).isEqualTo(expectedSecondCourse.getCourseTitle().getTitle());
        Assertions.assertThat(actualCourse.get().getVideoTitle()).isEqualTo(expectedSecondCourse.getVideoTitle());
        Assertions.assertThat(actualCourse.get().getVideoLink()).isEqualTo(expectedSecondCourse.getVideoLink());
        Assertions.assertThat(actualCourse.get().getText()).isEqualTo(expectedSecondCourse.getText());
        Assertions.assertThat(actualCourse.get().getPage()).isEqualTo(expectedSecondCourse.getPage());
    }

    @Test
    void shouldFindByVideoTitleContains() {
        Optional<List<Course>> actualCourse = courseRepository.findAllByVideoTitleContains("Video Title");

        Assertions.assertThat(actualCourse.get().size()).isEqualTo(6);
    }

    @Test
    void shouldFindAllByCourseTitleContains() {
        Optional<List<Course>> actualCourse = courseRepository.findAllByCourseTitle(courseTitle);

        Assertions.assertThat(actualCourse.get().size()).isEqualTo(5);
    }

    @Test
    void shouldExistsByCourseTitle() {
        boolean exists = courseRepository.existsByCourseTitle(expectedFirstCourse.getCourseTitle());

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void shouldExistsByVideoTitle() {
        boolean exists = courseRepository.existsByVideoTitle(expectedSecondCourse.getVideoTitle());

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void shouldExistsByCourseTitleAndPage() {
        boolean exists = courseRepository.existsByCourseTitleAndPage(expectedFirstCourse.getCourseTitle(), expectedFirstCourse.getPage());

        Assertions.assertThat(exists).isTrue();
    }
}