package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.repository.CourseTitleRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.CourseTitleService;
import com.github.vladyslavbabenko.mycoloroflife.service.RoleService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
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
    private final MessageSourceUtil messageSource;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public CourseTitleServiceImpl(CourseTitleRepository courseTitleRepository, RoleService roleService, UserService userService, MessageSourceUtil messageSource) {
        this.courseTitleRepository = courseTitleRepository;
        this.roleService = roleService;
        this.userService = userService;
        this.messageSource = messageSource;
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

        log.info("{} has been created", courseTitleToSave.getTitle());

        return true;
    }

    @Override
    public boolean delete(Integer courseTitleId) {
        if (!courseTitleRepository.existsById(courseTitleId)) {
            return false;
        }

        Optional<CourseTitle> courseTitleFromDB = courseTitleRepository.findById(courseTitleId);

        if (courseTitleFromDB.isEmpty()) {
            return false;
        }

        Role courseOwnerRoleByTitle = createCourseOwnerRoleByTitle(courseTitleFromDB.get(), false);
        userService.deleteRoleFromUser(userService.getAllUsers(), courseOwnerRoleByTitle);
        roleService.delete(courseOwnerRoleByTitle);
        courseTitleRepository.delete(courseTitleFromDB.get());

        log.info("{} has been deleted", courseTitleFromDB.get().getTitle());

        return true;
    }

    @Override
    public boolean update(CourseTitle updatedCourseTitle) {
        if (!courseTitleRepository.existsById(updatedCourseTitle.getId())) {
            return false;
        }

        Optional<CourseTitle> optionalCourseTitle = courseTitleRepository.findById(updatedCourseTitle.getId());

        if (optionalCourseTitle.isEmpty()) {
            return false;
        }

        CourseTitle courseTitleFromDB = optionalCourseTitle.get();
        roleService.update(createCourseOwnerRoleByTitle(courseTitleFromDB, false), createCourseOwnerRoleByTitle(updatedCourseTitle, false));
        courseTitleFromDB.setTitle(updatedCourseTitle.getTitle());
        courseTitleFromDB.setDescription(updatedCourseTitle.getDescription());
        courseTitleRepository.save(courseTitleFromDB);

        log.info("{} has been updated", courseTitleFromDB.getTitle());

        return true;
    }

    @Override
    public Role createCourseOwnerRoleByTitle(CourseTitle courseTitle, boolean saveToDB) {
        Role role = Role.builder()
                .roleName(messageSource.getMessage("role.course.owner") + roleService.convertToRoleStyle(courseTitle.getTitle()))
                .description(messageSource.getMessage("course.owner") + " " + "\"" + courseTitle.getTitle() + "\"")
                .build();

        if (saveToDB) {
            log.info("{} has been created", role.getRoleName());
            roleService.save(role);
        }

        return role;
    }
}