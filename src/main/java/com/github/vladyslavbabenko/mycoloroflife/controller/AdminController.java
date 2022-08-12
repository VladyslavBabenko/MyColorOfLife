package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

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
}