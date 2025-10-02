package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.ChangePasswordRequest;
import com.github.sidd6p.store.dtos.RegisterUserRequest;
import com.github.sidd6p.store.dtos.UpdateUserRequest;
import com.github.sidd6p.store.dtos.UserDto;
import com.github.sidd6p.store.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private final UserService userService;

    @GetMapping()
    @Operation(summary = "Get all users", description = "Retrieve a list of all users, optionally sorted by a specified field.")
    public List<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "", name = "sort") String sortBy) {
        return userService.getAllUsers(sortBy);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID.")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    @Operation(summary = "Create new user", description = "Register a new user in the system.")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegisterUserRequest registerUserRequest,
                                              UriComponentsBuilder uriBuilder) {
        var userDto = userService.createUser(registerUserRequest);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user's information.")
    public ResponseEntity<UserDto> updateUser(@PathVariable long id,
                                              @RequestBody UpdateUserRequest userUpdateRequest) {
        return userService.updateUser(id, userUpdateRequest)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Remove a user from the system.")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        if (userService.deleteUser(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/change-password")
    @Operation(summary = "Change user password", description = "Update a user's password.")
    public ResponseEntity<Void> updatePassword(@PathVariable("id") long id,
                                               @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        if (userService.changePassword(id, changePasswordRequest)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
