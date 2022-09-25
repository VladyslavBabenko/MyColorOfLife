package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseTitleRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.CourseTitleService;
import com.github.vladyslavbabenko.mycoloroflife.service.RoleService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CourseTitleService}.
 */

@Service
public class CourseTitleServiceImpl implements CourseTitleService {
    private final CourseTitleRepository courseTitleRepository;
    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public CourseTitleServiceImpl(CourseTitleRepository courseTitleRepository, RoleService roleService, UserService userService) {
        this.courseTitleRepository = courseTitleRepository;
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public Optional<CourseTitle> findByTitle(String title) {
        return courseTitleRepository.findByTitle(title);
    }

    @Override
    public List<CourseTitle> findAllByCourseTitleContains(String keyword) {
        return courseTitleRepository.findAllByTitleContains(keyword).orElse(new ArrayList<>());
    }

    @Override
    public List<CourseTitle> getAllCourseTitles() {
        List<CourseTitle> courseTitles = courseTitleRepository.findAll();
        courseTitles.sort(Comparator.comparingLong(CourseTitle::getId).reversed());
        return courseTitles;
    }

    @Override
    public Optional<CourseTitle> findById(Integer id) {
        return courseTitleRepository.findById(id);
    }

    @Override
    public boolean existsByTitle(String title) {
        return courseTitleRepository.existsByTitle(title);
    }

    @Override
    public boolean existsById(Integer id) {
        return courseTitleRepository.existsById(id);
    }

    @Override
    public boolean save(CourseTitle courseTitleToSave) {
        if (courseTitleRepository.existsByTitle(courseTitleToSave.getTitle())) {
            return false;
        }

        createCourseOwnerRoleByTitle(courseTitleToSave, true);
        courseTitleRepository.save(courseTitleToSave);
        return true;
    }

    @Override
    public boolean delete(Integer courseTitleId) {
        if (courseTitleRepository.existsById(courseTitleId)) {
            Optional<CourseTitle> courseTitleFromDB = courseTitleRepository.findById(courseTitleId);
            if (courseTitleFromDB.isPresent()) {
                Role courseOwnerRoleByTitle = createCourseOwnerRoleByTitle(courseTitleFromDB.get(), false);
                userService.deleteRoleFromUser(userService.getAllUsers(), courseOwnerRoleByTitle);
                roleService.delete(courseOwnerRoleByTitle);
                courseTitleRepository.delete(courseTitleFromDB.get());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean update(CourseTitle updatedCourseTitle) {
        if (courseTitleRepository.existsById(updatedCourseTitle.getId())) {
            Optional<CourseTitle> optionalCourseTitle = courseTitleRepository.findById(updatedCourseTitle.getId());
            if (optionalCourseTitle.isPresent()) {
                CourseTitle courseTitleFromDB = optionalCourseTitle.get();
                roleService.update(createCourseOwnerRoleByTitle(courseTitleFromDB, false), createCourseOwnerRoleByTitle(updatedCourseTitle, false));
                courseTitleFromDB.setTitle(updatedCourseTitle.getTitle());
                courseTitleFromDB.setDescription(updatedCourseTitle.getDescription());
                courseTitleRepository.save(courseTitleFromDB);
                return true;
            }
        }
        return false;
    }

    @Override
    public Role createCourseOwnerRoleByTitle(CourseTitle courseTitle, boolean saveToDB) {
        Role role = Role.builder()
                .roleName("ROLE_COURSE_OWNER_" + roleService.convertToRoleStyle(courseTitle.getTitle()))
                .description("Власник курсу " + "\"" + courseTitle.getTitle() + "\"")
                .build();
        if (saveToDB) {
            roleService.save(role);
        }
        return role;
    }
}