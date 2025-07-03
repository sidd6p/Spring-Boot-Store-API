package com.github.sidd6p.store.models;

import jakarta.persistence.*;
import lombok.*;


@Builder  // Lombok: generates a builder pattern implementation for the class
@Setter // Lombok: generates setter methods for all fields
@Getter // Lombok: generates getter methods for all fields
@Entity // JPA: marks this class as a database entity
// Lombok: generates a no-argument constructor
// Required by JPA/Hibernate for entity instantiation via reflection, even if @AllArgsConstructor is present
@NoArgsConstructor
@AllArgsConstructor // Lombok: generates a constructor with all fields
@Table(name = "users") // JPA: specifies the table name in the database
public class User {

    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies how the primary key should be generated (auto-incremented by the database)
    @Column(name = "id") // JPA: maps this field to a column in the database table
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
