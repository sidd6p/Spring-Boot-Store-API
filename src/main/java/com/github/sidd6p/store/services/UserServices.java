package com.github.sidd6p.store.services;

import com.github.sidd6p.store.entities.User;
import com.github.sidd6p.store.repositories.AddressRepository;
import com.github.sidd6p.store.repositories.ProfileRepository;
import com.github.sidd6p.store.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserServices {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final AddressRepository addressRepository;

    private final EntityManager entityManager;

    /**
     * The showEntityStates function demonstrates the concept of JPA entity states (transient, managed/persistent, detached)
     * by checking if a User entity is managed by the EntityManager before and after saving it.
     *
     * The @Transactional annotation is used to define a transactional boundary, ensuring that all operations
     * within the method are executed within a single database transaction. This is important for operations
     * that require atomicity and consistency, such as multiple database updates or lazy loading of entities.
     *
     */
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
}
