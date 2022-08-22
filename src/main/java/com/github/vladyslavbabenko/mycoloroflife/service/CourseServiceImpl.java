package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CourseService}.
 */

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        courses.sort(Comparator.comparingLong(Course::getId).reversed());
        return courses;
    }

    @Override
    public List<Course> findAllByCourseTitleContains(String keyword) {
        return courseRepository.findAllByCourseTitleContains(keyword).orElse(Collections.emptyList());
    }

    @Override
    public Course findById(Integer courseId) {
        return courseRepository.findById(courseId).orElse(new Course());
    }

    @Override
    public boolean saveCourse(Course courseToSave) {
        if (courseRepository.existsByCourseTitle(courseToSave.getCourseTitle()) &&
                courseToSave.getVideoTitle() != null &&
                courseToSave.getVideoTitle().length() > 0 &&
                courseRepository.existsByVideoTitle(courseToSave.getVideoTitle())) {
            return false;
        } else if (courseRepository.existsByCourseTitleAndPage(courseToSave.getCourseTitle(), courseToSave.getPage())) {
            return false;
        } else {
            courseRepository.save(courseToSave);
            return true;
        }
    }

    @Override
    public boolean deleteCourse(Integer courseId) {
        if (courseRepository.existsById(courseId)) {
            courseRepository.deleteById(courseId);
            return true;
        } else return false;
    }

    @Override
    public boolean updateCourse(Course updatedCourse) {
        Optional<Course> optionalCourse = courseRepository.findById(updatedCourse.getId());

        if (optionalCourse.isEmpty()) {
            return false;
        } else if (courseRepository.existsByCourseTitleAndPage(updatedCourse.getCourseTitle(), updatedCourse.getPage())) {
            return false;
        } else {
            Course courseToUpdate = optionalCourse.get();

            courseToUpdate.setCourseTitle(updatedCourse.getCourseTitle());
            courseToUpdate.setVideoTitle(updatedCourse.getVideoTitle());
            courseToUpdate.setVideoLink(updatedCourse.getVideoLink());
            courseToUpdate.setText(updatedCourse.getText());
            courseToUpdate.setPage(updatedCourse.getPage());

            courseRepository.save(courseToUpdate);
            return true;
        }
    }

    @Override
    public Optional<List<Course>> findAllByVideoTitleContains(String keyword) {
        return courseRepository.findAllByVideoTitleContains(keyword);
    }

    @Override
    public Optional<Course> findByCourseTitleAndPage(String courseTitle, Integer page) {
        return courseRepository.findByCourseTitleAndPage(courseTitle, page);
    }
}