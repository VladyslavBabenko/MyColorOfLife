package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.repository.ActivationCodeRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.implementation.ActivationCodeServiceImpl;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

@DisplayName("Unit-level testing for ActivationCodeService")
class ActivationCodeServiceImplTest {

    private User testUser;
    private ActivationCodeService codeService;
    private ActivationCodeRepository codeRepository;
    private ActivationCode testCode;
    private Course testCourse;
    private CourseTitle testCourseTitle;

    @BeforeEach
    void setUp() {
        //given
        codeRepository = Mockito.mock(ActivationCodeRepository.class);
        codeService = new ActivationCodeServiceImpl(codeRepository);

        testCourseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();

        testUser = User.builder().id(1).username("TestUser").email("TestUser@mail.com").build();

        testCourse = Course.builder()
                .id(1)
                .courseTitle(testCourseTitle)
                .page(1)
                .text("Test Text 1")
                .build();

        testCode = ActivationCode.builder()
                .id(1)
                .code("Q5sxTc941iokNy8")
                .user(testUser)
                .courseTitle(testCourseTitle)
                .build();
    }

    @Test
    void isActivationCodeServiceImplTestReady() {
        Assertions.assertThat(codeRepository).isNotNull().isInstanceOf(ActivationCodeRepository.class);
        Assertions.assertThat(codeService).isNotNull().isInstanceOf(ActivationCodeService.class);
        Assertions.assertThat(testUser).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(testCourse).isNotNull().isInstanceOf(Course.class);
    }

    @Test
    void getAll() {
        //when
        codeService.getAll();

        //then
        Mockito.verify(codeRepository, Mockito.times(1)).findAll();
    }

    @Test
    void findAllByUser() {
        //when
        codeService.findAllByUser(testUser);

        //then
        Mockito.verify(codeRepository, Mockito.times(1)).findAllByUser(testUser);
    }

    @Test
    void findById() {
        //when
        codeService.findById(testCode.getId());

        //then
        Mockito.verify(codeRepository, Mockito.times(1)).findById(testCode.getId());
    }

    @Test
    void findByCode() {
        //when
        codeService.findByCode(testCode.getCode());

        //then
        Mockito.verify(codeRepository, Mockito.times(1)).findByCode(testCode.getCode());
    }

    @Test
    void existsByCode() {
        //when
        Mockito.doReturn(true)
                .when(codeRepository)
                .existsByCode(testCode.getCode());

        boolean existsByCode = codeService.existsByCode(testCode.getCode());

        //then
        Mockito.verify(codeRepository, Mockito.times(1)).existsByCode(testCode.getCode());
        Assertions.assertThat(existsByCode).isTrue();
    }

    @Test
    void existsByUser() {
        //when
        Mockito.doReturn(true)
                .when(codeRepository)
                .existsByUser(testCode.getUser());

        boolean existsByUser = codeService.existsByUser(testCode.getUser());

        //then
        Mockito.verify(codeRepository, Mockito.times(1)).existsByUser(testCode.getUser());
        Assertions.assertThat(existsByUser).isTrue();
    }

    @Test
    void createCode() {
        ActivationCode code = codeService.createCode(testCourseTitle, testUser);

        //then
        Mockito.verify(codeRepository, Mockito.times(1)).save(code);
    }

    @Test
    void save_CodeExists() {
        Mockito.doReturn(true)
                .when(codeRepository)
                .existsByCode(testCode.getCode());

        boolean isSaved = codeService.save(testCode);

        Mockito.verify(codeRepository, Mockito.times(0)).save(testCode);
        Assertions.assertThat(isSaved).isFalse();
    }

    @Test
    void save() {
        boolean isSaved = codeService.save(testCode);

        Mockito.verify(codeRepository, Mockito.times(1)).save(testCode);
        Assertions.assertThat(isSaved).isTrue();
    }

    @Test
    void deleteAllByUser_WithNoUser() {
        boolean isDeleted = codeService.deleteAllByUser(testUser);

        Mockito.verify(codeRepository, Mockito.times(0)).deleteAllByUser(testUser);
        Assertions.assertThat(isDeleted).isFalse();
    }

    @Test
    void deleteAllByUser() {
        Mockito.doReturn(true)
                .when(codeRepository)
                .existsByUser(testUser);

        boolean isDeleted = codeService.deleteAllByUser(testUser);

        Mockito.verify(codeRepository, Mockito.times(1)).deleteAllByUser(testUser);
        Assertions.assertThat(isDeleted).isTrue();
    }

    @Test
    void deleteByCode_WithNoCode() {
        boolean isDeleted = codeService.deleteByCode(testCode.getCode());

        Mockito.verify(codeRepository, Mockito.times(0)).deleteByCode(testCode.getCode());
        Assertions.assertThat(isDeleted).isFalse();
    }

    @Test
    void deleteByCode() {
        Mockito.doReturn(true)
                .when(codeRepository)
                .existsByCode(testCode.getCode());

        boolean isDeleted = codeService.deleteByCode(testCode.getCode());

        Mockito.verify(codeRepository, Mockito.times(1)).deleteByCode(testCode.getCode());
        Assertions.assertThat(isDeleted).isTrue();
    }

    @Test
    void update() {
        Mockito.doReturn(Optional.of(testCode))
                .when(codeRepository)
                .findById(testCode.getId());

        boolean isUpdated = codeService.update(testCode);

        Mockito.verify(codeRepository, Mockito.times(1)).save(testCode);
        Assertions.assertThat(isUpdated).isTrue();
    }

    @Test
    void update_WithNoCode() {
        boolean isUpdated = codeService.update(testCode);

        Mockito.verify(codeRepository, Mockito.times(0)).save(testCode);
        Assertions.assertThat(isUpdated).isFalse();
    }
}