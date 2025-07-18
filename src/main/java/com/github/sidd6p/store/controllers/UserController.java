package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.UserDto;
import com.github.sidd6p.store.mappers.UserMapper;
import com.github.sidd6p.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
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
        return userRepository.findAll(Sort.by(sortBy).ascending())
                .stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()))
                .toList(); // converts Stream<UserDto> to List<UserDto>
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // manually creating ResponseEntity
        } else {
            return ResponseEntity.ok(userMapper.toDto(user)); // builder pattern for ResponseEntity
        }
    }

}
