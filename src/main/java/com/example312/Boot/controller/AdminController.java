package com.example312.Boot.controller;

import com.example312.Boot.dao.RoleDAO;
import com.example312.Boot.model.User;
import com.example312.Boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
//Сюда заходит только админ
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleDAO roleDAO;

    @Autowired
    public AdminController(UserService userService, RoleDAO roleDAO) {
        this.userService = userService;
        this.roleDAO = roleDAO;
    }

    @GetMapping
    public String getInfo(@AuthenticationPrincipal User admin, Model model) {
        model.addAttribute("user", admin);
        model.addAttribute("adminActive", "active");
        return "users/showUser";
    }

    @GetMapping("/users")
    public String getAllUsers(@AuthenticationPrincipal User admin, Model model) {
        model.addAttribute("roles", roleDAO.getAll());
        model.addAttribute("user", admin);
        model.addAttribute("emptyUser", new User());
        model.addAttribute("users", userService.getAllUsers());
        return "users/all";
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("adminActive", "active");
        return "users/showUser";
    }


    @GetMapping("/users/new")
    public String newUser(@ModelAttribute("userNew") User newUser,
                          @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("roles", roleDAO.getAll());
        model.addAttribute("user", user);
        return "users/new";
    }

    @PostMapping("/users")
    public String addUser(@ModelAttribute("userNew") User newUser, @ModelAttribute("user") User user, Model model) {
        boolean saved = userService.addUser(user);
        if (saved) {
            return "redirect:/admin/users";
        } else {
            model.addAttribute("saved", "Email уже используется");
            model.addAttribute("roles", roleDAO.getAll());
            model.addAttribute("user", user);
            return "users/new";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String edit(Model model, @PathVariable("id") long id) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleDAO.getAll());
        return "users/edit";
    }

    @PatchMapping("/users/{id}")
    public String update(@ModelAttribute("user") User updateUser, @PathVariable("id") long id) {
        userService.updateUser(id, updateUser); //Находим по id того юзера, которого надо изменить
        return "redirect:/admin/users";
    }


    @DeleteMapping("/users/{id}")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}
