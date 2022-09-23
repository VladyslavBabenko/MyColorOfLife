package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseTitleRepository;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

@DisplayName("Unit-level testing for CourseTitleService")
class CourseTitleServiceImplTest {

    private CourseTitleService courseTitleService;
    private CourseTitleRepository courseTitleRepository;
    private CourseTitle testCourseTitle;
    private RoleService roleService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        //given
        courseTitleRepository = Mockito.mock(CourseTitleRepository.class);
        roleService = Mockito.mock(RoleService.class);
        userService = Mockito.mock(UserService.class);

        courseTitleService = new CourseTitleServiceImpl(courseTitleRepository, roleService, userService);

        testCourseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();
    }

    @Test
    void isCourseTitleServiceImplTestReady() {
        Assertions.assertThat(courseTitleRepository).isNotNull().isInstanceOf(CourseTitleRepository.class);
        Assertions.assertThat(roleService).isNotNull().isInstanceOf(RoleService.class);
        Assertions.assertThat(userService).isNotNull().isInstanceOf(UserService.class);
        Assertions.assertThat(courseTitleService).isNotNull().isInstanceOf(CourseTitleService.class);
        Assertions.assertThat(testCourseTitle).isNotNull().isInstanceOf(CourseTitle.class);
    }

    @Test
    void findByTitle() {
        //given
        Mockito.doReturn(Optional.ofNullable(testCourseTitle)).when(courseTitleRepository).findByTitle(testCourseTitle.getTitle());

        //when
        courseTitleService.findByTitle(testCourseTitle.getTitle());

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).findByTitle(testCourseTitle.getTitle());
    }

    @Test
    void findAllByCourseTitleContains() {
        //given
        Mockito.doReturn(Optional.of(List.of(testCourseTitle))).when(courseTitleRepository).findAllByTitleContains(testCourseTitle.getTitle());

        //when
        courseTitleService.findAllByCourseTitleContains(testCourseTitle.getTitle());

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).findAllByTitleContains(testCourseTitle.getTitle());
    }

    @Test
    void getAllCourseTitles() {
        //when
        courseTitleService.getAllCourseTitles();

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).findAll();
        Assertions.assertThat(courseTitleService.getAllCourseTitles()).isSorted();
    }

    @Test
    void findById() {
        //given
        Mockito.doReturn(Optional.ofNullable(testCourseTitle)).when(courseTitleRepository).findById(testCourseTitle.getId());

        //when
        courseTitleService.findById(testCourseTitle.getId());

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).findById(testCourseTitle.getId());
    }

    @Test
    void existsByTitle() {
        //given
        Mockito.doReturn(true).when(courseTitleRepository).existsByTitle(testCourseTitle.getTitle());

        //when
        courseTitleService.existsByTitle(testCourseTitle.getTitle());

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).existsByTitle(testCourseTitle.getTitle());
    }

    @Test
    void existsById() {
        //given
        Mockito.doReturn(true).when(courseTitleRepository).existsById(testCourseTitle.getId());

        //when
        courseTitleService.existsById(testCourseTitle.getId());

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).existsById(testCourseTitle.getId());
    }

    @Test
    void save_WithExistsByTitleTrue() {
        //given
        Mockito.doReturn(true).when(courseTitleRepository).existsByTitle(testCourseTitle.getTitle());

        //when
        boolean isFalse = courseTitleService.save(testCourseTitle);

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).existsByTitle(testCourseTitle.getTitle());
        Mockito.verify(courseTitleRepository, Mockito.times(0)).save(testCourseTitle);
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void save_WithExistsByTitleFalse() {
        //given
        Mockito.doReturn(false).when(courseTitleRepository).existsByTitle(testCourseTitle.getTitle());

        //when
        boolean isTrue = courseTitleService.save(testCourseTitle);

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).existsByTitle(testCourseTitle.getTitle());
        Mockito.verify(courseTitleRepository, Mockito.times(1)).save(testCourseTitle);
        Assertions.assertThat(isTrue).isTrue();
    }

    @Test
    void delete_WithExistsByIdFalse() {
        //given
        Mockito.doReturn(false).when(courseTitleRepository).existsById(testCourseTitle.getId());

        //when
        boolean isFalse = courseTitleService.delete(testCourseTitle.getId());

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).existsById(testCourseTitle.getId());
        Mockito.verify(courseTitleRepository, Mockito.times(0)).deleteById(testCourseTitle.getId());
        Assertions.assertThat(isFalse).isFalse();

    }

    @Test
    void delete_WithExistsByIdTrue() {
        //given
        Mockito.doReturn(true).when(courseTitleRepository).existsById(testCourseTitle.getId());
        Mockito.doReturn(Optional.of(testCourseTitle)).when(courseTitleRepository).findById(testCourseTitle.getId());

        //when
        boolean isTrue = courseTitleService.delete(testCourseTitle.getId());

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).existsById(testCourseTitle.getId());
        Mockito.verify(courseTitleRepository, Mockito.times(1)).findById(testCourseTitle.getId());
        Mockito.verify(courseTitleRepository, Mockito.times(1)).delete(testCourseTitle);

        Assertions.assertThat(isTrue).isTrue();
    }

    @Test
    void update_WithExistsByIdFalse() {
        //given
        Mockito.doReturn(false).when(courseTitleRepository).existsById(testCourseTitle.getId());

        //when
        boolean isFalse = courseTitleService.update(testCourseTitle);

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).existsById(testCourseTitle.getId());
        Mockito.verify(courseTitleRepository, Mockito.times(0)).findById(testCourseTitle.getId());
        Mockito.verify(courseTitleRepository, Mockito.times(0)).save(testCourseTitle);
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void update_WithExistsByIdTrue() {
        //given
        Mockito.doReturn(true).when(courseTitleRepository).existsById(testCourseTitle.getId());
        Mockito.doReturn(Optional.ofNullable(testCourseTitle)).when(courseTitleRepository).findById(testCourseTitle.getId());

        //when
        boolean isTrue = courseTitleService.update(testCourseTitle);

        //then
        Mockito.verify(courseTitleRepository, Mockito.times(1)).existsById(testCourseTitle.getId());
        Mockito.verify(courseTitleRepository, Mockito.times(1)).findById(testCourseTitle.getId());
        Mockito.verify(courseTitleRepository, Mockito.times(1)).save(testCourseTitle);
        Assertions.assertThat(isTrue).isTrue();
    }
}