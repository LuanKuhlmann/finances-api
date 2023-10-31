package io.github.luankuhlmann.myfinances.controller;

import io.github.luankuhlmann.myfinances.dto.UserDTO;
import io.github.luankuhlmann.myfinances.exception.AuthenticationError;
import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.User;
import io.github.luankuhlmann.myfinances.service.EntriesService;
import io.github.luankuhlmann.myfinances.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final EntriesService entriesService;

    public UserController(UserService userService, EntriesService entriesService) {
        this.userService = userService;
        this.entriesService = entriesService;
    }

    @PostMapping
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

    @GetMapping("balance/{id}")
    public ResponseEntity getBalance(@PathVariable("id") Long id) {
        Optional<User> user = userService.findById(id);

        if(user.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(entriesService.getBalancePerUser(id));
    }
}
