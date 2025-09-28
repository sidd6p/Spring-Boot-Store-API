package com.github.sidd6p.store.services;

import com.github.sidd6p.store.dtos.ChangePasswordRequest;
import com.github.sidd6p.store.dtos.RegisterUserRequest;
import com.github.sidd6p.store.dtos.UpdateUserRequest;
import com.github.sidd6p.store.dtos.UserDto;
import com.github.sidd6p.store.entities.Address;
import com.github.sidd6p.store.entities.User;
import com.github.sidd6p.store.mappers.UserMapper;
import com.github.sidd6p.store.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserServices implements UserDetailsService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers(String sortBy) {
        if (!Set.of("id", "name", "email").contains(sortBy)) {
            sortBy = "id";
        }
        log.info("Getting all users sorted by: {}", sortBy);
        return userRepository.findAll(Sort.by(sortBy).ascending()).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public Optional<UserDto> getUserById(long id) {
        log.info("Getting user with id: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    public UserDto createUser(RegisterUserRequest registerUserRequest) {
        log.info("Creating user with details: {}", registerUserRequest);

        if (userRepository.existsByEmail(registerUserRequest.getEmail())) {
            log.error("User with email {} already exists", registerUserRequest.getEmail());
            throw new IllegalArgumentException("User with this email already exists");
        }

        var user = userMapper.toEntity(registerUserRequest);
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public Optional<UserDto> updateUser(long id, UpdateUserRequest userUpdateRequest) {
        log.info("Updating user with id {} with details: {}", id, userUpdateRequest);

        return userRepository.findById(id)
                .map(user -> {
                    user.updateFromRequest(userUpdateRequest.getUser_name(), userUpdateRequest.getEmail());
                    userRepository.save(user);
                    return userMapper.toDto(user);
                });
    }

    public boolean deleteUser(long id) {
        log.info("Deleting user with id {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    public boolean changePassword(long id, ChangePasswordRequest changePasswordRequest) {
        log.info("Updating password for user with id {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    if (user.changePassword(changePasswordRequest.getOldPassword(),
                                          changePasswordRequest.getNewPassword())) {
                        userRepository.save(user);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }


    // Legacy methods for demonstration purposes - can be kept or moved to a separate demo service
    @Transactional
    public void showEntityStates(){
        var user1 = User.builder()
                .name("Siddharth Purwar")
                .email("siddpurwar@gmail.com")
                .password("Siddharth")
                .build();

        if (entityManager.contains(user1)) {
            System.out.println("User1 is in the managed/persistent state.");
        } else {
            System.out.println("User1 is in the transient/detached state.");
        }
        userRepository.save(user1);
        if (entityManager.contains(user1)) {
            System.out.println("User1 is in the managed/persistent state after save.");
        } else {
            System.out.println("User1 is in the transient/detached state after save.");
        }
    }

    public void showRelatedEntities() {
        userRepository.findById(1L).ifPresent(System.out::println);
        profileRepository.findById(1L).ifPresent(System.out::println);
    }

    public void fetchAddress() {
        addressRepository.findById(1L).ifPresent(System.out::println);
    }

    @Transactional
    public void persistRelated() {
        var user = User.builder()
                .name("Siddharth Purwar")
                .email("siddpurwar@gmail.com")
                .password("Siddharth")
                .build();
        var address = Address.builder()
                .city("Delhi")
                .zip("110001")
                .street("Delhi")
                .build();
        user.addAddress(address);
        userRepository.save(user);
    }

    @Transactional
    public void deleteRelated() {
        userRepository.findById(1L).ifPresent(user -> {
            addressRepository.deleteAll(user.getAddresses());
            userRepository.delete(user);
        });
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // No roles/authorities for simplicity
        );
    }
}
