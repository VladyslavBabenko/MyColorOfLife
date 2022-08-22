package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.service.CourseService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;

    @GetMapping()
    public String userList(Model model) {
        model.addAttribute("listOfUsers", userService.getAllUsers());
        return "adminTemplate/adminPanelPage";
    }

    @GetMapping("/findByID")
    public String findUserByID(@RequestParam("userID") String userId, Model model) {
        int id = -1;

        if (StringUtils.isNumeric(userId)) {
            id = Integer.parseInt(userId);
        } else if (userId != null && !userId.isEmpty()) {
            model.addAttribute("findIdError", "Недійсний ідентифікатор користувача");
        }

        model.addAttribute("listOfUsers", id > 0 ? userService.findById(id) : userService.getAllUsers());

        return "adminTemplate/adminPanelPage";
    }

    @DeleteMapping()
    public String deleteUserById(@RequestParam("userID") String userId, Model model) {
        int id;

        if (userId != null && !userId.isEmpty() && StringUtils.isNumeric(userId)) {
            id = Integer.parseInt(userId);
            if (id > 0) {
                if (!userService.deleteUser(id)) {
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
        return "adminTemplate/courseAdminPage";
    }

    @GetMapping("/course/{courseId}/edit")
    public String editCoursePage(@PathVariable("courseId") Integer id, Model model) {
        model.addAttribute("course", courseService.findById(id));
        return "adminTemplate/editCoursePage";
    }

    @GetMapping("/course/new")
    public String newCoursePage(Model model) {
        model.addAttribute("course", new Course());
        return "adminTemplate/addCoursePage";
    }

    @PostMapping("/course")
    public String addCourse(@ModelAttribute @Valid Course course, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "adminTemplate/addCoursePage";
        }

        if (!courseService.saveCourse(course)) {
            model.addAttribute("courseError", "Така сторінка вже існує");
            return "adminTemplate/addCoursePage";
        }

        return "redirect:/admin/course";
    }

    @PutMapping("/course")
    public String updateCourse(@ModelAttribute @Valid Course course, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "adminTemplate/editCoursePage";
        }

        if (!courseService.updateCourse(course)) {
            model.addAttribute("courseError", "Перевірте коректність введених даних");
            return "adminTemplate/editCoursePage";
        }

        return "redirect:/admin/course";
    }

    @DeleteMapping("/course")
    public String deleteEvent(@RequestParam("courseID") Integer courseId) {
        courseService.deleteCourse(courseId);

        return "redirect:/admin/course";
    }
}