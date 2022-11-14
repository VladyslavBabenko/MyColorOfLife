package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseProgress;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseProgressRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.CourseProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CourseProgressService}.
 */

@Service
public class CourseProgressServiceImpl implements CourseProgressService {

    private final CourseProgressRepository courseProgressRepository;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
        }

        courseProgressRepository.save(courseProgressToSave);

        log.info("New CourseProgress object created for course {} and user with username - {}", courseProgressToSave.getCourse().getCourseTitle().getTitle(), courseProgressToSave.getUser().getUsername());

        return true;
    }

    @Override
    public boolean delete(Integer courseProgressId) {
        if (courseProgressRepository.existsById(courseProgressId)) {
            courseProgressRepository.deleteById(courseProgressId);

            log.info("CourseProgress with id {} successfully deleted", courseProgressId);

            return true;
        }

        return false;
    }

    @Override
    public boolean update(CourseProgress updatedCourseProgress) {
        Optional<CourseProgress> optionalCourseProgress = courseProgressRepository.findById(updatedCourseProgress.getId());

        if (optionalCourseProgress.isEmpty()) {
            return false;
        }

        CourseProgress courseProgressToUpdate = optionalCourseProgress.get();

        courseProgressToUpdate.setCourse(updatedCourseProgress.getCourse());
        courseProgressToUpdate.setUser(updatedCourseProgress.getUser());

        courseProgressRepository.save(courseProgressToUpdate);

        log.info("CourseProgress for course {} and user {} successfully updated",
                optionalCourseProgress.get().getCourse().getCourseTitle().getTitle(), optionalCourseProgress.get().getUser().getUsername());

        return true;
    }
}