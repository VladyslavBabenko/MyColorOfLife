package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
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

@DisplayName("Integration-level testing for ActivationCodeRepository")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ActivationCodeRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    private User testUser;
    private ActivationCode expectedFirstCode, expectedSecondCode;
    private CourseTitle testCourseTitle, differentCourseTitle;
    @Autowired
    private ActivationCodeRepository codeRepository;

    @BeforeEach
    void setUp() {
        testCourseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();
        differentCourseTitle = CourseTitle.builder().id(2).title("Different Course").build();

        testUser = User.builder().id(1).username("TestUser").email("TestUser@mail.com").build();

        Course expectedFirstCourse = Course.builder()
                .id(1)
                .courseTitle(testCourseTitle)
                .page(1)
                .text("Test Text 1")
                .build();

        Course expectedSecondCourse = Course.builder()
                .id(6)
                .courseTitle(differentCourseTitle)
                .page(1)
                .text("Different Course Test Text 1")
                .build();

        expectedFirstCode = ActivationCode.builder()
                .id(1)
                .code("Q5sxTc941iokNy8")
                .user(testUser)
                .courseTitle(testCourseTitle)
                .build();

        expectedSecondCode = ActivationCode.builder()
                .id(2)
                .code("Yx5ui2nqx98m92x")
                .user(testUser)
                .courseTitle(differentCourseTitle)
                .build();
    }

    @Test
    void findAllByUser() {
        Optional<List<ActivationCode>> codeList = codeRepository.findAllByUser(testUser);

        Assertions.assertThat(codeList.get()).isNotNull().hasSize(2);
    }

    @Test
    void findByCode() {
        Optional<ActivationCode> code = codeRepository.findByCode(expectedFirstCode.getCode());

        Assertions.assertThat(code.get()).isNotNull().isInstanceOf(ActivationCode.class);
        Assertions.assertThat(code.get().getCode()).isEqualTo(expectedFirstCode.getCode());
        Assertions.assertThat(code.get().getCourseTitle().getTitle()).isEqualTo(expectedFirstCode.getCourseTitle().getTitle());
        Assertions.assertThat(code.get().getUser().getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void existsByCode() {
        boolean existsByCode = codeRepository.existsByCode(expectedFirstCode.getCode());

        Assertions.assertThat(existsByCode).isTrue();
    }

    @Test
    void existsByUser() {
        boolean existsByUser = codeRepository.existsByUser(testUser);

        Assertions.assertThat(existsByUser).isTrue();
    }

    @Test
    void deleteAllByUser() {
        codeRepository.deleteAllByUser(testUser);

        Assertions.assertThat(codeRepository.existsByUser(testUser)).isFalse();
    }

    @Test
    void deleteByCode() {
        codeRepository.deleteByCode(expectedFirstCode.getCode());

        Assertions.assertThat(codeRepository.existsByCode(expectedFirstCode.getCode())).isFalse();
    }
}