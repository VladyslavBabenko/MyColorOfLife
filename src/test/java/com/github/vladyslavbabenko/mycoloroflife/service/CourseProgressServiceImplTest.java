package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.*;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseProgressRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.implementation.CourseProgressServiceImpl;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@DisplayName("Unit-level testing for CourseProgressService")
class CourseProgressServiceImplTest {
    private CourseProgressRepository courseProgressRepository;
    private CourseProgressService courseProgressService;

    private User testUser;
    private Role testRole;
    private Course testCourse;
    private CourseTitle testCourseTitle;
    private CourseProgress testCourseProgress;

    @BeforeEach
    void setUp() {
        //given
        courseProgressRepository = Mockito.mock(CourseProgressRepository.class);
        courseProgressService = new CourseProgressServiceImpl(courseProgressRepository);

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        testUser = User.builder()
                .id(1)
                .username("TestUser")
                .email("TestMail@mail.com")
                .roles(roles)
                .password(String.valueOf(123456789))
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        testRole = Role.builder().id(1).roleName("ROLE_USER").build();
        testCourseTitle = CourseTitle.builder().id(1).title("test").build();
        testCourse = Course.builder().id(1).courseTitle(testCourseTitle).text("test text").build();
        testCourseProgress = CourseProgress.builder().id(1).course(testCourse).user(testUser).build();
    }

    @Test
    void isCourseProgressServiceImplTestReady() {
        Assertions.assertThat(courseProgressRepository).isNotNull().isInstanceOf(CourseProgressRepository.class);
        Assertions.assertThat(courseProgressService).isNotNull().isInstanceOf(CourseProgressService.class);
        Assertions.assertThat(testUser).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(testRole).isNotNull().isInstanceOf(Role.class);
        Assertions.assertThat(testCourseTitle).isNotNull().isInstanceOf(CourseTitle.class);
        Assertions.assertThat(testCourse).isNotNull().isInstanceOf(Course.class);
        Assertions.assertThat(testCourseProgress).isNotNull().isInstanceOf(CourseProgress.class);
    }

    @Test
    void getAllCourseProgress() {
        //when
        List<CourseProgress> courseProgressList = courseProgressService.getAllCourseProgress();

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).findAll();
        Assertions.assertThat(courseProgressList)
                .isNotNull()
                .isInstanceOf(List.class)
                .isSorted();
    }

    @Test
    void findAllByUser() {
        //when
        courseProgressService.findAllByUser(testUser);

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).findAllByUser(testUser);
    }

    @Test
    void findByUserAndCourse() {
        //when
        courseProgressService.findByUserAndCourse(testUser, testCourse);

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).findByUserAndCourse(testUser, testCourse);
    }

    @Test
    void existsByUserAndCourse() {
        //when
        courseProgressService.existsByUserAndCourse(testUser, testCourse);

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).existsByUserAndCourse(testUser, testCourse);
    }

    @Test
    void existsByUser() {
        //when
        courseProgressService.existsByUser(testUser);

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).existsByUser(testUser);
    }

    @Test
    void save_Failure_ExistsByUserAndCourse_True() {
        //given
        Mockito.doReturn(true).when(courseProgressRepository).existsByUserAndCourse(testCourseProgress.getUser(), testCourseProgress.getCourse());

        //when
        boolean saved = courseProgressService.save(testCourseProgress);

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).existsByUserAndCourse(testCourseProgress.getUser(), testCourseProgress.getCourse());
        Mockito.verify(courseProgressRepository, Mockito.times(0)).save(testCourseProgress);
        Assertions.assertThat(saved).isFalse();
    }

    @Test
    void save_Success() {
        //given
        Mockito.doReturn(false).when(courseProgressRepository).existsByUserAndCourse(testCourseProgress.getUser(), testCourseProgress.getCourse());

        //when
        boolean saved = courseProgressService.save(testCourseProgress);

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).existsByUserAndCourse(testCourseProgress.getUser(), testCourseProgress.getCourse());
        Mockito.verify(courseProgressRepository, Mockito.times(1)).save(testCourseProgress);
        Assertions.assertThat(saved).isTrue();
    }

    @Test
    void delete_Success() {
        //given
        Mockito.doReturn(true).when(courseProgressRepository).existsById(testCourseProgress.getId());

        //when
        boolean deleted = courseProgressService.delete(testCourseProgress.getId());

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).existsById(testCourseProgress.getId());
        Mockito.verify(courseProgressRepository, Mockito.times(1)).deleteById(testCourseProgress.getId());
        Assertions.assertThat(deleted).isTrue();
    }

    @Test
    void delete_Failure_ExistsById_False() {
        //given
        Mockito.doReturn(false).when(courseProgressRepository).existsById(testCourseProgress.getId());

        //when
        boolean deleted = courseProgressService.delete(testCourseProgress.getId());

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).existsById(testCourseProgress.getId());
        Mockito.verify(courseProgressRepository, Mockito.times(0)).deleteById(testCourseProgress.getId());
        Assertions.assertThat(deleted).isFalse();
    }

    @Test
    void update_Failure_FindById_IsEmpty() {
        //given
        Mockito.doReturn(Optional.empty()).when(courseProgressRepository).findById(testCourseProgress.getId());

        //when
        boolean updated = courseProgressService.update(testCourseProgress);

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).findById(testCourseProgress.getId());
        Mockito.verify(courseProgressRepository, Mockito.times(0)).save(testCourseProgress);
        Assertions.assertThat(updated).isFalse();
    }

    @Test
    void update_Success() {
        //given
        Mockito.doReturn(Optional.of(testCourseProgress)).when(courseProgressRepository).findById(testCourseProgress.getId());

        //when
        boolean updated = courseProgressService.update(testCourseProgress);

        //then
        Mockito.verify(courseProgressRepository, Mockito.times(1)).findById(testCourseProgress.getId());
        Mockito.verify(courseProgressRepository, Mockito.times(1)).save(testCourseProgress);
        Assertions.assertThat(updated).isTrue();
    }
}