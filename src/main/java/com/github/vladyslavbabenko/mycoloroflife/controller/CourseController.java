package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.*;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * {@link Controller} for courses.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {
    private final UserService userService;
    private final RoleService roleService;
    private final CourseService courseService;
    private final MessageSourceUtil messageSource;
    private final CourseTitleService courseTitleService;
    private final CourseProgressService courseProgressService;

    @GetMapping(path = {"", "/page/{pageId}"})
    public String getCourses(Model model, String keyword, @PathVariable(value = "pageId", required = false) Integer pageId) {
        if (pageId == null) {
            pageId = 1;
        }

        List<CourseTitle> courseTitles;

        if (keyword != null) {
            courseTitles = courseTitleService.findAllByCourseTitleContains(keyword);
        } else {
            courseTitles = courseTitleService.getAllCourseTitles();
        }

        List<CourseTitle> listOfCourseTitles = courseTitles.stream().skip(pageId * 6L - 6).limit(6).collect(Collectors.toList());

        int[] numberOfPages = new int[(int) Math.ceil(courseTitles.size() / 6.0)];

        for (int i = 0; i < numberOfPages.length; i++) {
            numberOfPages[i] = i;
        }

        model.addAttribute("listOfCourseTitles", listOfCourseTitles);
        model.addAttribute("pageID", pageId);
        model.addAttribute("numberOfPages", numberOfPages);

        return messageSource.getMessage("template.course.all");
    }

    @GetMapping("/{courseTitle}")
    public String getCourseMain(Model model, @PathVariable(value = "courseTitle") String title) {
        Optional<CourseTitle> optionalCourseTitle = courseTitleService.findByTitle(title);
        if (optionalCourseTitle.isPresent()) {
            List<Course> courseList = courseService.findAllByCourseTitle(optionalCourseTitle.get());
            courseList.sort(Comparator.comparingLong(Course::getPage));
            User currentUser = userService.getCurrentUser();
            AtomicInteger lastVisitedPage = new AtomicInteger(-1);

            if (currentUser.getCourseProgresses() != null) {
                currentUser.getCourseProgresses()
                        .stream()
                        .filter(courseProgress -> courseProgress.getCourse().getCourseTitle().equals(optionalCourseTitle.get()))
                        .findFirst().ifPresent(value -> lastVisitedPage.set(value.getCourse().getPage()));
            }

            model.addAttribute("lastVisitedPage", lastVisitedPage.intValue());
            model.addAttribute("courseTitle", optionalCourseTitle.get());
            model.addAttribute("courseList", courseList);

            return messageSource.getMessage("template.course.main");
        } else {
            return messageSource.getMessage("template.error.404");
        }
    }

    @GetMapping("/{courseTitle}/page/{pageID}")
    public String getCoursePage(Model model, @PathVariable(value = "courseTitle") String courseTitle, @PathVariable(value = "pageID") String pageIDAsString) {
        if (courseTitleService.existsByTitle(courseTitle)) {
            String courseOwnerAuthority = messageSource.getMessage("role.course.owner") + roleService.convertToRoleStyle(courseTitle);
            if (roleService.existsByRoleName(courseOwnerAuthority)) {
                Role role = roleService.findByRoleName(courseOwnerAuthority).orElse(new Role());
                User userFromDB = userService.getCurrentUser();
                if (userFromDB.getRoles() != null && userFromDB.getRoles().contains(role)) {
                    int pageID = 1;

                    if (StringUtils.isNumeric(pageIDAsString)) {
                        pageID = Integer.parseInt(pageIDAsString);
                    }

                    Optional<CourseTitle> optionalCourseTitle = courseTitleService.findByTitle(courseTitle);
                    if (optionalCourseTitle.isPresent()) {

                        Optional<Course> course = courseService.findByCourseTitleAndPage(optionalCourseTitle.get().getTitle(), pageID);

                        if (course.isPresent()) {
                            AtomicInteger lastVisitedPage = new AtomicInteger(-1);

                            if (userFromDB.getCourseProgresses() != null) {
                                userFromDB.getCourseProgresses()
                                        .stream()
                                        .filter(courseProgress -> courseProgress.getCourse().getCourseTitle().equals(optionalCourseTitle.get()))
                                        .findFirst().ifPresent(value -> lastVisitedPage.set(value.getCourse().getPage()));
                            }

                            if (lastVisitedPage.intValue() < course.get().getPage()) {
                                model.addAttribute("tooEarly", messageSource.getMessage("user.course.too-early"));
                            } else {
                                model.addAttribute("courseTitle", optionalCourseTitle.get());
                                model.addAttribute("course", course.get());
                                model.addAttribute("lastVisitedPage", lastVisitedPage.intValue());
                            }

                            int lastCoursePage = -1;
                            List<Course> courseList = courseService.findAllByCourseTitle(optionalCourseTitle.get());
                            if (!courseList.isEmpty()) {
                                courseList.sort(Comparator.comparingLong(Course::getPage).reversed());
                                lastCoursePage = courseList.get(0).getPage();
                            }

                            model.addAttribute("lastCoursePage", lastCoursePage);

                        } else {
                            return messageSource.getMessage("template.error.404");
                        }
                    } else {
                        return messageSource.getMessage("template.error.404");
                    }
                    return messageSource.getMessage("template.course.page");
                } else return messageSource.getMessage("template.error.access-denied");
            } else return messageSource.getMessage("template.error.access-denied");
        } else return messageSource.getMessage("template.error.404");
    }

    @PatchMapping("/{courseTitle}/page/{pageID}")
    private String patchCourseProgressUpdate(@PathVariable("courseTitle") String courseTitleAsString, @PathVariable("pageID") String pageIDAsString) {
        int page = -1;

        if (StringUtils.isNumeric(pageIDAsString)) {
            page = Integer.parseInt(pageIDAsString);
        }

        Optional<Course> optionalCourse = courseService.findByCourseTitleAndPage(courseTitleAsString, page);
        if (optionalCourse.isPresent()) {
            page += 1;

            Optional<Course> optionalNextCourseFromDB = courseService.findByCourseTitleAndPage(courseTitleAsString, page);
            if (optionalNextCourseFromDB.isPresent()) {
                Optional<CourseProgress> courseProgressFromDB = courseProgressService.findByUserAndCourse(userService.getCurrentUser(), optionalCourse.get());

                if (courseProgressFromDB.isPresent()) {
                    courseProgressFromDB.get().setCourse(optionalNextCourseFromDB.get());
                    courseProgressService.update(courseProgressFromDB.get());
                } else {
                    page -= 1;
                }
            }
        }

        return "redirect:/course/" + courseTitleAsString + "/page/" + page;
    }
}