package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;
    private RoleService roleService;
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

       @GetMapping("/list")
    public String allUsers(Model model){
        List<User> listUsers = userService.getAllUser();
        model.addAttribute("users", listUsers);

        return "/list";
    }

    @GetMapping("/add")
    public String addUserPage(User user){
        return "/add";
    }

    @PostMapping("/add")
    public String addUser( @ModelAttribute("user") User user) {
        Set<Role> roleSet = new HashSet<>();
        for (Role role : user.getRoleSet()){
            role = roleService.findRoleByName(role.getRolesName());
            roleSet.add(role);
        }
        user.setRoleSet(roleSet);
        if (!userService.findUserByEmail(user.getEmail()).isPresent()) {
            userService.create(user);

            return "redirect:/admin/list";
        } else {
            return "/userExists";
        }
    }

    @GetMapping("/edit/{id}")
    public String editUserPage(@PathVariable("id") long id, Model model){
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "/edit";
    }

    @PostMapping("/edit/{id}")
    public String editUser(
            @ModelAttribute("user") User user
    )
    {
        //Установка новых ролей
        Set<Role> roleSet = new HashSet<>();
        for (Role role: user.getRoleSet()) {
            role = roleService.findRoleByName(role.getRolesName());
            roleSet.add(role);
        }
        user.setRoleSet(roleSet);
        if(!userService.findUserByEmail(user.getEmail()).isPresent()
                || userService.findUserByEmail1(user.getEmail()).getId().equals(user.getId()))
        {
            userService.update(user);
            return "redirect:/admin/list";
        } else {
            return "/userExists";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id){
        User user = userService.findUserById(id);
        userService.delete(user);
        return "redirect:/admin/list";
    }

    @RequestMapping(value="/logout" , method = RequestMethod.POST)
    public String logoutPage(
    )
    {
        return "/login";
    }
}
