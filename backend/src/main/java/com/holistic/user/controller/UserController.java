package com.holistic.user.controller;

import com.holistic.user.dto.UserDto;
import com.holistic.user.model.User;
import com.holistic.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        // Map DTO to Entity
        User user = new User(null, userDto.name(), userDto.email(), userDto.password());
        User savedUser = userService.createUser(user);

        // Map Entity back to DTO for the response
        UserDto savedUserDto = new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), null);
        return new ResponseEntity<>(savedUserDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail(), null))
                .map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail(), null))
                .collect(Collectors.toList());
    }
}