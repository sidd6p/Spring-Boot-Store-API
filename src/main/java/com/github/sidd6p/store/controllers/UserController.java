package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.RegisterUserRequest;
import com.github.sidd6p.store.dtos.UpdateUserRequest;
import com.github.sidd6p.store.dtos.UserDto;
import com.github.sidd6p.store.mappers.UserMapper;
import com.github.sidd6p.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @GetMapping()
    public List<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "", name = "sort") String sortBy) {
        // We use stream() to process the List<User> from userRepository in a functional way.
        // stream() converts the List into a Stream, which allows us to use map(), filter(), etc.
        // map() transforms each User object into a UserDto object.
        // toList() collects the results from the Stream back into a List.
        // This approach is useful for transforming or filtering data efficiently.
        // Original list -> Stream -> map() -> Stream -> toList() -> List result
        if (!Set.of("id", "name", "email").contains(sortBy)) {
            sortBy = "id";
        }
        log.info("Getting all users sorted by: {}", sortBy);
        return userRepository.findAll(Sort.by(sortBy).ascending()).stream().map(user -> new UserDto(user.getId(), user.getName(), user.getEmail())).toList(); // converts Stream<UserDto> to List<UserDto>
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.error("User with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // manually creating ResponseEntity
        } else {
            log.info("User with id {} found", id);
            return ResponseEntity.ok(userMapper.toDto(user)); // builder pattern for ResponseEntity
        }
    }

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestHeader("x-auth-token") String authToken,
                                              @RequestBody RegisterUserRequest registerUserRequest,
                                              UriComponentsBuilder uriBuilder) {
        log.info("Creating user with details: {}", registerUserRequest);
        var user = userMapper.toEntity(registerUserRequest);
        userRepository.save(user);

        // Use UriComponentsBuilder to construct the URI for the newly created user
        // 1. Specify the path template with a placeholder for the user ID.
        // 2. Replace the placeholder with the actual user ID using buildAndExpand().
        // 3. Convert the constructed URI to a java.net.URI object.
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();

        var UserDto = userMapper.toDto(user);

        // Return a ResponseEntity with the created status, the URI in the Location header, and the UserDto in the body
        return ResponseEntity.created(uri).body(UserDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestHeader("x-auth-token") String authToken,
                                              @PathVariable long id,
                                              @RequestBody UpdateUserRequest userUpdateRequest) {
        log.info("Updating user with id {} with details: {}", id, userUpdateRequest);
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            userMapper.update(userUpdateRequest, user);
            userRepository.save(user);
            return ResponseEntity.ok(userMapper.toDto(user));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        log.info("Deleting user with id {}", id);
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            userRepository.delete(user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
