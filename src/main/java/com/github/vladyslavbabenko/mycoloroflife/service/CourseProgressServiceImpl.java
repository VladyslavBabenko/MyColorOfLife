package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseProgress;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CourseProgressService}.
 */

@Service
public class CourseProgressServiceImpl implements CourseProgressService {

    private final CourseProgressRepository courseProgressRepository;

    @Autowired
    public CourseProgressServiceImpl(CourseProgressRepository courseProgressRepository) {
        this.courseProgressRepository = courseProgressRepository;
    }

    @Override
    public List<CourseProgress> getAllCourseProgress() {
        List<CourseProgress> courseProgresses = courseProgressRepository.findAll();
        courseProgresses.sort(Comparator.comparingLong(CourseProgress::getId).reversed());
        return courseProgresses;
    }

    @Override
    public Optional<List<CourseProgress>> findAllByUser(User user) {
        return courseProgressRepository.findAllByUser(user);
    }

    @Override
    public Optional<CourseProgress> findByUserAndCourse(User user, Course course) {
        return courseProgressRepository.findByUserAndCourse(user, course);
    }

    @Override
    public boolean existsByUserAndCourse(User user, Course course) {
        return courseProgressRepository.existsByUserAndCourse(user, course);
    }

    @Override
    public boolean existsByUser(User user) {
        return courseProgressRepository.existsByUser(user);
    }

    @Override
    public boolean save(CourseProgress courseProgressToSave) {
        if (courseProgressRepository.existsByUserAndCourse(courseProgressToSave.getUser(), courseProgressToSave.getCourse())) {
            return false;
        } else {
            courseProgressRepository.save(courseProgressToSave);
            return true;
        }
    }

    @Override
    public boolean delete(Integer courseProgressId) {
        if (courseProgressRepository.existsById(courseProgressId)) {
            courseProgressRepository.deleteById(courseProgressId);
            return true;
        } else return false;
    }

    @Override
    public boolean update(CourseProgress updatedCourseProgress) {
        Optional<CourseProgress> optionalCourseProgress = courseProgressRepository.findById(updatedCourseProgress.getId());

        if (optionalCourseProgress.isEmpty()) {
            return false;
        } else {
            CourseProgress courseProgressToUpdate = optionalCourseProgress.get();

            courseProgressToUpdate.setCourse(updatedCourseProgress.getCourse());
            courseProgressToUpdate.setUser(updatedCourseProgress.getUser());

            courseProgressRepository.save(courseProgressToUpdate);
            return true;
        }
    }
}