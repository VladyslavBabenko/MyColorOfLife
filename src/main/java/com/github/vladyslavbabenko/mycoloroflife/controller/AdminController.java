package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import com.github.vladyslavbabenko.mycoloroflife.service.implementation.MailSenderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ActivationCodeService codeService;
    private final CourseService courseService;
    private final MailSenderServiceImpl mailSender;
    private final CourseTitleService courseTitleService;

    @GetMapping()
    public String userList(Model model) {
        model.addAttribute("listOfUsers", userService.getAllUsers());
        return "adminTemplate/adminPanelPage";
    }

    @GetMapping("/find-by-id")
    public String findUserByID(@RequestParam("userID") String id, Model model) {
        int userId = -1;

        if (StringUtils.isNumeric(id)) {
            userId = Integer.parseInt(id);
        } else {
            model.addAttribute("findIdError", "Недійсний ідентифікатор користувача");
        }

        model.addAttribute("listOfUsers", userId > 0 ? userService.findById(userId) : userService.getAllUsers());

        return "adminTemplate/adminPanelPage";
    }

    @DeleteMapping("/delete")
    public String deleteUserById(@RequestParam("userID") String id, Model model) {
        int userId;

        if (StringUtils.isNumeric(id)) {
            userId = Integer.parseInt(id);
            if (userId > 0) {
                if (!userService.deleteUser(userId)) {
                    model.addAttribute("deleteIdError", "Недійсний ідентифікатор користувача");
                    model.addAttribute("listOfUsers", userService.getAllUsers());
                    return "adminTemplate/adminPanelPage";
                }
            }
        } else {
            model.addAttribute("deleteIdError", "Недійсний ідентифікатор користувача");
            model.addAttribute("listOfUsers", userService.getAllUsers());
            return "adminTemplate/adminPanelPage";
        }

        return "redirect:/admin";
    }

    @GetMapping("/course")
    public String coursePage(Model model) {
        model.addAttribute("listOfCourses", courseService.getAllCourses());
        model.addAttribute("listOfCourseTitles", courseTitleService.getAllCourseTitles());
        return "adminTemplate/courseAdminPage";
    }

    @GetMapping("/course/new")
    public String createCoursePage(Model model) {
        model.addAttribute("course", new Course());
        return "adminTemplate/addCoursePage";
    }

    @PostMapping("/course/new")
    public String createCourse(@ModelAttribute @Valid Course course, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "adminTemplate/addCoursePage";
        }

        if (!courseService.save(course)) {
            model.addAttribute("courseError", "Така сторінка вже існує");
            return "adminTemplate/addCoursePage";
        }

        return "redirect:/admin/course";
    }

    @GetMapping("/course/{courseId}/edit")
    public String editCoursePage(@PathVariable("courseId") String courseId, Model model) {
        if (StringUtils.isNumeric(courseId)) {
            model.addAttribute("course", courseService.findById(Integer.parseInt(courseId)));
        }
        return "adminTemplate/editCoursePage";
    }

    @PutMapping("/course/edit")
    public String updateCourse(@ModelAttribute @Valid Course course, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "adminTemplate/editCoursePage";
        }

        if (!courseService.update(course)) {
            model.addAttribute("courseError", "Перевірте коректність введених даних");
            return "adminTemplate/editCoursePage";
        }

        return "redirect:/admin/course";
    }

    @DeleteMapping("/course/delete")
    public String deleteCourse(@RequestParam("courseID") String courseId) {
        if (StringUtils.isNumeric(courseId)) {
            courseService.delete(Integer.parseInt(courseId));
        }
        return "redirect:/admin/course";
    }

    @DeleteMapping("/code/delete")
    public String deleteCode(@RequestParam("activationCode") String code, Model model) {
        if (code != null && !code.isEmpty() && codeService.existsByCode(code)) {
            codeService.deleteByCode(code);
        } else {
            model.addAttribute("codeError", "Перевірте коректність введених даних");
            model.addAttribute("listOfUsers", userService.getAllUsers());
            return "adminTemplate/adminPanelPage";
        }

        return "redirect:/admin";
    }

    @PostMapping("/code/new")
    public String generateCode(@RequestParam("userID") String userIDAsString, @RequestParam("courseTitleID") String courseTitleIDAsString, Model model) {
        int userID = -1;
        int courseTitleID = -1;

        if (StringUtils.isNumeric(userIDAsString)) {
            userID = Integer.parseInt(userIDAsString);
            if (StringUtils.isNumeric(courseTitleIDAsString)) {
                courseTitleID = Integer.parseInt(courseTitleIDAsString);
            }
        }

        if (userService.existsById(userID)) {
            if (courseTitleService.existsById(courseTitleID)) {
                Optional<CourseTitle> courseTitleFromDB = courseTitleService.findById(courseTitleID);
                if (courseTitleFromDB.isPresent()) {
                    User user = userService.findById(userID);
                    if (codeService.findAllByUserAndCourseTitle(user, courseTitleFromDB.get()).isEmpty()) {

                        ActivationCode code = codeService.createCode(courseTitleFromDB.get(), userService.findById(userID));

                        String htmlText = "<div style=\"text-align:center;line-height:24px\"> " +
                                "<span style=\"font-size:25px;\">" +
                                "<p>Здрастуйте, " + user.getUsername() + "!</p>" +
                                "<p> " +
                                "Дякуємо за покупку курсу " + code.getCourseTitle().getTitle() +
                                "</p>" +
                                "<p style = \"font-size:35px;line-height:40px;font-weight:bold\">" +
                                "Ваш код активації: <br>" + code.getCode() +
                                "</p>" +
                                "</div>";

                        mailSender.sendEmail(user.getEmail(),
                                "Код активації для курсу " + code.getCourseTitle().getTitle(),
                                htmlText);
                    } else {
                        model.addAttribute("codeForCourseExists", "Код цього курсу вже існує для користувача");
                        model.addAttribute("listOfUsers", userService.getAllUsers());
                        return "adminTemplate/adminPanelPage";
                    }
                }
            } else {
                model.addAttribute("courseTitleIDError", "Такого курсу не існує");
                model.addAttribute("listOfUsers", userService.getAllUsers());
                return "adminTemplate/adminPanelPage";
            }
        } else {
            model.addAttribute("userIDError", "Такого користувача не існує");
            model.addAttribute("listOfUsers", userService.getAllUsers());
            return "adminTemplate/adminPanelPage";
        }

        return "redirect:/admin";
    }

    @GetMapping("/course-title/new")
    public String createCourseTitlePage(Model model) {
        model.addAttribute("courseTitle", new CourseTitle());
        return "adminTemplate/addCourseTitlePage";
    }

    @PostMapping("/course-title/new")
    public String createCourseTitle(@ModelAttribute @Valid CourseTitle courseTitle, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "adminTemplate/addCourseTitlePage";
        }

        if (!courseTitleService.save(courseTitle)) {
            model.addAttribute("courseTitleError", "Така назва вже існує");
            return "adminTemplate/addCourseTitlePage";
        }

        return "redirect:/admin/course";
    }

    @DeleteMapping("/course-title/delete")
    public String deleteCourseTitle(@RequestParam("courseTitleID") String courseTitleIDAsString, Model model) {
        int courseTitleID = -1;

        if (StringUtils.isNumeric(courseTitleIDAsString)) {
            courseTitleID = Integer.parseInt(courseTitleIDAsString);
        }

        if (courseTitleID > 0 && courseTitleService.existsById(courseTitleID)) {
            Optional<CourseTitle> courseTitleFromDB = courseTitleService.findById(courseTitleID);
            if (courseTitleFromDB.isPresent()) {
                if (courseService.findAllByCourseTitle(courseTitleFromDB.get()).isEmpty()) {
                    courseTitleService.delete(courseTitleID);
                } else {
                    model.addAttribute("linksByCourseTitleError", "Видаліть існуючі посилання на назву");
                    model.addAttribute("listOfCourses", courseService.getAllCourses());
                    model.addAttribute("listOfCourseTitles", courseTitleService.getAllCourseTitles());
                    return "adminTemplate/courseAdminPage";
                }
            }
        } else {
            model.addAttribute("courseTitleIDError", "Такого курсу не існує");
            model.addAttribute("listOfCourses", courseService.getAllCourses());
            model.addAttribute("listOfCourseTitles", courseTitleService.getAllCourseTitles());
            return "adminTemplate/courseAdminPage";
        }

        return "redirect:/admin/course";
    }

    @GetMapping("/course-title/{courseTitleID}/edit")
    public String editCourseTitlePage(@PathVariable("courseTitleID") String courseTitleIDAsString, Model model) {
        int courseTitleID = -1;

        if (StringUtils.isNumeric(courseTitleIDAsString)) {
            courseTitleID = Integer.parseInt(courseTitleIDAsString);
        }

        if (courseTitleService.existsById(courseTitleID)) {
            Optional<CourseTitle> courseTitle = courseTitleService.findById(courseTitleID);
            if (courseTitle.isPresent()) {
                model.addAttribute("courseTitle", courseTitle.get());
                return "adminTemplate/editCourseTitlePage";
            }
        }

        return "redirect:/admin/course";
    }

    @PutMapping("/course-title/edit")
    public String updateCourseTitle(@ModelAttribute @Valid CourseTitle courseTitle, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "adminTemplate/editCourseTitlePage";
        }

        if (!courseTitleService.update(courseTitle)) {
            model.addAttribute("courseTitleError", "Перевірте коректність введених даних");
            return "adminTemplate/editCourseTitlePage";
        }

        return "redirect:/admin/course";
    }
}