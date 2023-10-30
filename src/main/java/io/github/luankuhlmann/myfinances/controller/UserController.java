package io.github.luankuhlmann.myfinances.controller;

import io.github.luankuhlmann.myfinances.dto.UserDTO;
import io.github.luankuhlmann.myfinances.exception.AuthenticationError;
import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.User;
import io.github.luankuhlmann.myfinances.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity save(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        try {
            User registeredUser = userService.saveUser(user);
            return new ResponseEntity(registeredUser, HttpStatus.CREATED);
        }catch (BusinessRuleException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity authenticate(@RequestBody UserDTO userDTO) {
        try {
            User anthenticatedUser = userService.authentication(userDTO.getEmail(), userDTO.getPassword());
            return ResponseEntity.ok(anthenticatedUser);
        }catch (AuthenticationError e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
