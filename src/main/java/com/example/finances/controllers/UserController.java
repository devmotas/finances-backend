package com.example.finances.controllers;

import com.example.finances.domains.user.UserCreateDTO;
import com.example.finances.domains.user.UserDTO;
import com.example.finances.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping()
    public UserDTO createUser(@RequestBody UserCreateDTO userCreateDTO) {
        return userService.createUser(userCreateDTO);
    }
}
