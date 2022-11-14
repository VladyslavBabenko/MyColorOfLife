package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseRepository;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseTitleRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * Implementation of {@link CourseService}.
 */

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseTitleRepository courseTitleRepository;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, CourseTitleRepository courseTitleRepository) {
        this.courseRepository = courseRepository;
        this.courseTitleRepository = courseTitleRepository;
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        courses.sort(Comparator.comparingLong(Course::getId).reversed());
        return courses;
    }

    @Override
    public List<Course> findAllByCourseTitleContains(String title) {
        Optional<CourseTitle> courseTitleFromDB = courseTitleRepository.findByTitle(title);
        if (courseTitleFromDB.isEmpty()) {
            courseTitleFromDB = Optional.of(new CourseTitle());
        }
        return courseRepository.findAllByCourseTitle(courseTitleFromDB.get()).orElse(Collections.emptyList());
    }

    @Override
    public Course findById(Integer courseId) {
        return courseRepository.findById(courseId).orElse(new Course());
    }

    /*if (courseRepository.existsByCourseTitle(courseToSave.getCourseTitle()) &&
                courseToSave.getVideoTitle() != null &&
                courseToSave.getVideoTitle().length() > 0 &&
                courseRepository.existsByVideoTitle(courseToSave.getVideoTitle())) {
            return false;
        } else if (courseRepository.existsByCourseTitleAndPage(courseToSave.getCourseTitle(), courseToSave.getPage())) {
            return false;
        } else {
            courseRepository.save(courseToSave);
            log.info("Page {} for {} has been created", courseToSave.getPage(), courseToSave.getCourseTitle());
            return true;
        }*/

    @Override
    public boolean save(Course courseToSave) {
        if (courseRepository.existsByCourseTitle(courseToSave.getCourseTitle()) &&
                courseToSave.getVideoTitle() != null &&
                courseToSave.getVideoTitle().length() > 0 &&
                courseRepository.existsByVideoTitle(courseToSave.getVideoTitle())) {
            return false;
        }

        if (courseRepository.existsByCourseTitleAndPage(courseToSave.getCourseTitle(), courseToSave.getPage())) {
            return false;
        }

        courseRepository.save(courseToSave);

        log.info("Page {} for {} has been created", courseToSave.getPage(), courseToSave.getCourseTitle());

        return true;
    }

    @Override
    public boolean delete(Integer courseId) {
        if (courseRepository.existsById(courseId)) {
            courseRepository.deleteById(courseId);

            log.info("Page with id - {} has been deleted", courseId);

            return true;
        }

        return false;
    }

    @Override
    public boolean update(Course updatedCourse) {
        Optional<Course> optionalCourse = courseRepository.findById(updatedCourse.getId());

        if (optionalCourse.isEmpty()) {
            return false;
        }

        if (courseRepository.existsByCourseTitleAndPage(updatedCourse.getCourseTitle(), updatedCourse.getPage())) {
            return false;
        }

        Course courseToUpdate = optionalCourse.get();

        courseToUpdate.setCourseTitle(updatedCourse.getCourseTitle());
        courseToUpdate.setVideoTitle(updatedCourse.getVideoTitle());
        courseToUpdate.setVideoLink(updatedCourse.getVideoLink());
        courseToUpdate.setText(updatedCourse.getText());
        courseToUpdate.setPage(updatedCourse.getPage());

        courseRepository.save(courseToUpdate);

        log.info("Page {} for {} has been updated", optionalCourse.get().getPage(), optionalCourse.get().getCourseTitle());

        return true;

    }

    @Override
    public List<Course> findAllByCourseTitle(CourseTitle courseTitle) {
        return courseRepository.findAllByCourseTitle(courseTitle).orElse(new ArrayList<>());
    }

    @Override
    public Optional<List<Course>> findAllByVideoTitleContains(String keyword) {
        return courseRepository.findAllByVideoTitleContains(keyword);
    }

    @Override
    public Optional<Course> findByCourseTitleAndPage(String title, Integer page) {
        Optional<CourseTitle> optionalCourseTitle = courseTitleRepository.findByTitle(title);

        if (optionalCourseTitle.isPresent()) {
            return courseRepository.findByCourseTitleAndPage(optionalCourseTitle.get(), page);
        }

        return Optional.empty();
    }

    @Override
    public boolean existsByCourseTitle(CourseTitle courseTitle) {
        return courseRepository.existsByCourseTitle(courseTitle);
    }
}