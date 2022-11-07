package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import com.github.vladyslavbabenko.mycoloroflife.service.implementation.MailSenderServiceImpl;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link Controller} to manage users and courses.
 * Only ROLE_ADMIN has access to it
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final MessageSourceUtil messageSource;
    private final MailSenderServiceImpl mailSender;
    private final ActivationCodeService codeService;
    private final CourseTitleService courseTitleService;
    private final MailContentBuilderService mailContentBuilderService;

    @GetMapping()
    public String getUsers(Model model) {
        model.addAttribute("listOfUsers", userService.getAllUsers());
        return messageSource.getMessage("template.admin.panel.user");
    }

    @GetMapping("/find-by-id")
    public String getUser(@RequestParam("userID") String id, Model model) {
        int userId = -1;

        if (StringUtils.isNumeric(id)) {
            userId = Integer.parseInt(id);
        } else {
            model.addAttribute("getUserInvalidID", messageSource.getMessage("user.invalid.id"));
        }

        model.addAttribute("listOfUsers", userId > 0 ? userService.findById(userId) : userService.getAllUsers());

        return messageSource.getMessage("template.admin.panel.user");
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam("userID") String id, Model model) {

        model.addAttribute("listOfUsers", userService.getAllUsers());

        if (StringUtils.isNumeric(id)) {

            int userId = Integer.parseInt(id);

            if (userId > 0 && !userService.deleteUser(userId)) {
                model.addAttribute("deleteUserInvalidID", messageSource.getMessage("user.invalid.id"));
                return messageSource.getMessage("template.admin.panel.user");
            }
        } else {
            model.addAttribute("deleteUserInvalidID", messageSource.getMessage("user.invalid.id"));
            return messageSource.getMessage("template.admin.panel.user");
        }

        return getUsers(model);
    }

    @GetMapping("/course")
    public String getCourses(Model model) {
        model.addAttribute("listOfCourses", courseService.getAllCourses());
        model.addAttribute("listOfCourseTitles", courseTitleService.getAllCourseTitles());
        return messageSource.getMessage("template.admin.panel.course");
    }

    @GetMapping("/course/new")
    public String getAddCoursePage(Model model) {
        model.addAttribute("course", new Course());
        return messageSource.getMessage("template.admin.course.add");
    }

    @PostMapping("/course/new")
    public String postAddCourse(@ModelAttribute @Valid Course course, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.admin.course.add");
        }

        if (!courseService.save(course)) {
            model.addAttribute("courseExistsPage", messageSource.getMessage("course.page.exists"));
            return messageSource.getMessage("template.admin.course.add");
        }

        return getCourses(model);
    }

    @GetMapping("/course/{courseId}/edit")
    public String getCourseEdit(@PathVariable("courseId") String courseId, Model model) {
        if (StringUtils.isNumeric(courseId)) {
            model.addAttribute("course", courseService.findById(Integer.parseInt(courseId)));
        }
        return messageSource.getMessage("template.admin.course.edit");
    }

    @PutMapping("/course/edit")
    public String putCourseUpdate(@ModelAttribute @Valid Course course, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.admin.course.edit");
        }

        if (!courseService.update(course)) {
            model.addAttribute("courseInvalidInput", messageSource.getMessage("invalid.input"));
            return messageSource.getMessage("template.admin.course.edit");
        }

        return getCourses(model);
    }

    @DeleteMapping("/course/delete")
    public String deleteCourse(@RequestParam("courseID") String courseId, Model model) {
        if (StringUtils.isNumeric(courseId)) {
            if (!courseService.delete(Integer.parseInt(courseId))) {
                model.addAttribute("coursePageNotFound", messageSource.getMessage("course.page.not.exists"));
            }
        }

        return getCourses(model);
    }

    @DeleteMapping("/code/delete")
    public String deleteCode(@RequestParam("activationCode") String code, Model model) {
        if (code != null && !code.isEmpty() && codeService.existsByCode(code)) {
            codeService.deleteByCode(code);
        } else {
            model.addAttribute("codeInvalidInput", messageSource.getMessage("invalid.input"));
            model.addAttribute("listOfUsers", userService.getAllUsers());
            return messageSource.getMessage("template.admin.panel.user");
        }

        return getUsers(model);
    }

    @PostMapping("/code/new")
    public String postGenerateCode(@RequestParam("userID") String userIDAsString, @RequestParam("courseTitleID") String courseTitleIDAsString, Model model) {
        int userID = -1;
        int courseTitleID = -1;

        if (StringUtils.isNumeric(userIDAsString)) {
            userID = Integer.parseInt(userIDAsString);
            if (StringUtils.isNumeric(courseTitleIDAsString)) {
                courseTitleID = Integer.parseInt(courseTitleIDAsString);
            }
        }

        if (!userService.existsById(userID)) {
            model.addAttribute("userNotFound", messageSource.getMessage("user.not.found"));
            model.addAttribute("listOfUsers", userService.getAllUsers());
            return messageSource.getMessage("template.admin.panel.user");
        }

        if (!courseTitleService.existsById(courseTitleID)) {
            model.addAttribute("courseNotFound", messageSource.getMessage("course.title.not.exists"));
            model.addAttribute("listOfUsers", userService.getAllUsers());
            return messageSource.getMessage("template.admin.panel.user");
        }

        Optional<CourseTitle> courseTitleFromDB = courseTitleService.findById(courseTitleID);

        if (courseTitleFromDB.isPresent()) {

            User user = userService.findById(userID);

            if (!codeService.findAllByUserAndCourseTitle(user, courseTitleFromDB.get()).isEmpty()) {
                model.addAttribute("userActivationCodeExists", messageSource.getMessage("user.activation-code.exists"));
                model.addAttribute("listOfUsers", userService.getAllUsers());
                return messageSource.getMessage("template.admin.panel.user");
            }

            ActivationCode code = codeService.createCode(courseTitleFromDB.get(), userService.findById(userID));

            String courseTitle = code.getCourseTitle().getTitle();

            List<String> strings = new ArrayList<>();
            strings.add(user.getName());
            strings.add(courseTitle);
            strings.add(code.getCode());

            mailSender.sendEmail(user.getEmail(),
                    messageSource.getMessage("email.course.activation-code.subject") + " " + courseTitle,
                    mailContentBuilderService.build(strings, messageSource.getMessage("template.email.activation-code")));

            model.addAttribute("mailHasBeenSent", messageSource.getMessage("user.activation-code.email.sent"));

        }

        return getUsers(model);
    }

    @GetMapping("/course-title/new")
    public String getAddCourseTitle(Model model) {
        model.addAttribute("courseTitle", new CourseTitle());
        return messageSource.getMessage("template.admin.course.title.add");
    }

    @PostMapping("/course-title/new")
    public String postAddCourseTitle(@ModelAttribute @Valid CourseTitle courseTitle, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.admin.course.title.add");
        }

        if (!courseTitleService.save(courseTitle)) {
            model.addAttribute("courseTitleExists", messageSource.getMessage("course.title.exists"));
            return messageSource.getMessage("template.admin.course.title.add");
        }

        return getCourses(model);
    }

    @DeleteMapping("/course-title/delete")
    public String deleteCourseTitle(@RequestParam("courseTitleID") String courseTitleIDAsString, Model model) {
        int courseTitleID = -1;

        if (StringUtils.isNumeric(courseTitleIDAsString)) {
            courseTitleID = Integer.parseInt(courseTitleIDAsString);
        }

        if (courseTitleID < 0 && !courseTitleService.existsById(courseTitleID)) {
            model.addAttribute("courseNotFound", messageSource.getMessage("course.title.not.exists"));
            model.addAttribute("listOfCourses", courseService.getAllCourses());
            model.addAttribute("listOfCourseTitles", courseTitleService.getAllCourseTitles());
            return messageSource.getMessage("template.admin.panel.course");
        }

        Optional<CourseTitle> courseTitleFromDB = courseTitleService.findById(courseTitleID);

        if (courseTitleFromDB.isPresent()) {
            if (courseService.findAllByCourseTitle(courseTitleFromDB.get()).isEmpty()) {
                courseTitleService.delete(courseTitleID);
            } else {
                model.addAttribute("courseLinksExists", messageSource.getMessage("course.exists.links"));
                model.addAttribute("listOfCourses", courseService.getAllCourses());
                model.addAttribute("listOfCourseTitles", courseTitleService.getAllCourseTitles());
                return messageSource.getMessage("template.admin.panel.course");
            }
        }

        return getCourses(model);
    }

    @GetMapping("/course-title/{courseTitleID}/edit")
    public String getCourseTitleEdit(@PathVariable("courseTitleID") String courseTitleIDAsString, Model model) {
        int courseTitleID = -1;

        if (StringUtils.isNumeric(courseTitleIDAsString)) {
            courseTitleID = Integer.parseInt(courseTitleIDAsString);
        }

        if (courseTitleService.existsById(courseTitleID)) {
            Optional<CourseTitle> courseTitle = courseTitleService.findById(courseTitleID);
            if (courseTitle.isPresent()) {
                model.addAttribute("courseTitle", courseTitle.get());
                return messageSource.getMessage("template.admin.course.title.edit");
            }
        }

        return getCourses(model);
    }

    @PutMapping("/course-title/edit")
    public String putCourseTitleUpdate(@ModelAttribute @Valid CourseTitle courseTitle, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.admin.course.title.edit");
        }

        if (!courseTitleService.update(courseTitle)) {
            model.addAttribute("courseTitleInvalidInput", messageSource.getMessage("invalid.input"));
            return messageSource.getMessage("template.admin.course.title.edit");
        }

        return getCourses(model);
    }
}