package com.github.sidd6p.store.services;

import com.github.sidd6p.store.entities.Address;
import com.github.sidd6p.store.entities.User;
import com.github.sidd6p.store.repositories.*;
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

    /**
     * The @Transactional annotation is used here to ensure that the persistence of the User and Address entities,
     * as well as the management of their relationship, occurs within a single database transaction. This guarantees
     * atomicity and consistency, so that either all changes are committed together or none are, preventing partial updates
     * in case of an error. It is especially important when persisting related entities and their associations.
     */
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

    /**
     * The @Transactional annotation is added to the deleteRelated() method to ensure that the method runs within an active
     * Hibernate session, fixing the LazyInitializationException. This guarantees that the User and Address entities
     * are properly loaded and managed within the transaction, allowing for their correct deletion.
     */
    @Transactional
    public void deleteRelated() {
        userRepository.findById(1L).ifPresent(user -> {;
            addressRepository.deleteAll(user.getAddresses());
            userRepository.delete(user);
        });
    }
}
