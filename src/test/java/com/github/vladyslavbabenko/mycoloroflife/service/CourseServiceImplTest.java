package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseRepository;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

@DisplayName("Unit-level testing for CourseService")
class CourseServiceImplTest {

    private CourseService courseService;
    private CourseRepository courseRepository;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        //given
        courseRepository = Mockito.mock(CourseRepository.class);
        courseService = new CourseServiceImpl(courseRepository);

        testCourse = Course.builder().id(2).courseTitle("Test").page(2).videoTitle("Test Video Title 2").videoLink("Test Video Link 2").text("Test Text 2").build();
    }

    @Test
    void isArticleServiceImplTestReady() {
        Assertions.assertThat(courseRepository).isNotNull().isInstanceOf(CourseRepository.class);
        Assertions.assertThat(courseService).isNotNull().isInstanceOf(CourseService.class);
        Assertions.assertThat(testCourse).isNotNull().isInstanceOf(Course.class);
    }

    @Test
    void getAllCourses() {
        //when
        courseService.getAllCourses();

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).findAll();
        Assertions.assertThat(courseService.getAllCourses()).isSorted();
    }

    @Test
    void findAllByCourseTitleContains() {
        //given
        String keyword = "Test";

        //when
        courseService.findAllByCourseTitleContains(keyword);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).findAllByCourseTitleContains(keyword);
    }

    @Test
    void findById() {
        //given
        int id = 1;

        //when
        courseService.findById(id);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void saveCourse_WhenCourseWithSameCourseTitleAndPageExists() {
        //given
        Mockito.doReturn(false).when(courseRepository).existsByCourseTitle(testCourse.getCourseTitle());
        Mockito.doReturn(false).when(courseRepository).existsByVideoTitle(testCourse.getVideoTitle());
        Mockito.doReturn(true).when(courseRepository).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());

        //when
        boolean isFalse = courseService.saveCourse(testCourse);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).existsByCourseTitle(testCourse.getCourseTitle());
        Mockito.verify(courseRepository, Mockito.times(1)).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());
        Mockito.verify(courseRepository, Mockito.times(0)).save(testCourse);
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void saveCourse_WhenCourseWithSameCourseTitleAndVideoTitleExists() {
        //given
        Mockito.doReturn(true).when(courseRepository).existsByCourseTitle(testCourse.getCourseTitle());
        Mockito.doReturn(true).when(courseRepository).existsByVideoTitle(testCourse.getVideoTitle());

        //when
        boolean isFalse = courseService.saveCourse(testCourse);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).existsByCourseTitle(testCourse.getCourseTitle());
        Mockito.verify(courseRepository, Mockito.times(1)).existsByVideoTitle(testCourse.getVideoTitle());
        Mockito.verify(courseRepository, Mockito.times(0)).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());
        Mockito.verify(courseRepository, Mockito.times(0)).save(testCourse);
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void saveCourseSuccess() {
        //given
        Mockito.doReturn(false).when(courseRepository).existsByCourseTitle(testCourse.getCourseTitle());
        Mockito.doReturn(false).when(courseRepository).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());

        //when
        boolean isTrue = courseService.saveCourse(testCourse);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).existsByCourseTitle(testCourse.getCourseTitle());
        Mockito.verify(courseRepository, Mockito.times(1)).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());
        Mockito.verify(courseRepository, Mockito.times(1)).save(testCourse);
        Assertions.assertThat(isTrue).isTrue();
    }

    @Test
    void deleteCourse_WhenCourseDoesNotExist() {
        //given
        int id = 1;
        Mockito.doReturn(false).when(courseRepository).existsById(id);

        //when
        boolean isFalse = courseService.deleteCourse(id);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(courseRepository, Mockito.times(0)).deleteById(id);
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void deleteCourse_WhenCourseExists() {
        //given
        int id = 1;
        Mockito.doReturn(true).when(courseRepository).existsById(id);

        //when
        boolean isTrue = courseService.deleteCourse(id);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(courseRepository, Mockito.times(1)).deleteById(id);
        Assertions.assertThat(isTrue).isTrue();
    }

    @Test
    void updateCourse_WhenCourseAlreadyExistsByCourseTitleAndPage() {
        //given
        Mockito.doReturn(Optional.of(testCourse)).when(courseRepository).findById(testCourse.getId());
        Mockito.doReturn(true).when(courseRepository).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());

        //when
        boolean isFalse = courseService.updateCourse(testCourse);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).findById(testCourse.getId());
        Mockito.verify(courseRepository, Mockito.times(1)).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());
        Mockito.verify(courseRepository, Mockito.times(0)).save(testCourse);
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void updateCourse_WhenCourseDoesNotExsitById() {
        //given
        Mockito.doReturn(Optional.empty()).when(courseRepository).findById(testCourse.getId());

        //when
        boolean isFalse = courseService.updateCourse(testCourse);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).findById(testCourse.getId());
        Mockito.verify(courseRepository, Mockito.times(0)).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());
        Mockito.verify(courseRepository, Mockito.times(0)).save(testCourse);
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void updateCourseSuccess() {
        //given
        Mockito.doReturn(Optional.of(testCourse)).when(courseRepository).findById(testCourse.getId());
        Mockito.doReturn(false).when(courseRepository).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());

        //when
        boolean isTrue = courseService.updateCourse(testCourse);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).findById(testCourse.getId());
        Mockito.verify(courseRepository, Mockito.times(1)).existsByCourseTitleAndPage(testCourse.getCourseTitle(), testCourse.getPage());
        Mockito.verify(courseRepository, Mockito.times(1)).save(testCourse);
        Assertions.assertThat(isTrue).isTrue();
    }

    @Test
    void findAllByVideoTitleContains() {
        //given
        String keyword = "Test";

        //when
        courseService.findAllByVideoTitleContains(keyword);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).findAllByVideoTitleContains(keyword);
    }

    @Test
    void findByCourseTitleAndPage() {
        //given
        String courseTitle = "Test";
        int page = 1;

        //when
        courseService.findByCourseTitleAndPage(courseTitle, page);

        //then
        Mockito.verify(courseRepository, Mockito.times(1)).findByCourseTitleAndPage(courseTitle, page);
    }
}