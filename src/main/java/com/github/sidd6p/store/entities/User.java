package com.github.sidd6p.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password", nullable = true)
    private String password;

    @OneToMany(mappedBy = "user", cascade =  CascadeType.PERSIST)
    @Builder.Default // Lombok: initializes the addresses list to an empty ArrayList by default
    private List<Address> addresses = new ArrayList<>();

    public void addAddress(Address address) {
        addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setUser(null); // Clear the user reference in the address
    }


    @ManyToMany()
    @JoinTable(
            name = "user_tags", // Join table name
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key column for User
            inverseJoinColumns = @JoinColumn(name = "tag_id") // Foreign key column for Tag
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

   public void addTag(Tag tag) {
        tags.add(tag);
        tag.getUsers().add(this); // Ensure the reverse relationship is maintained
    }
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getUsers().remove(this); // Ensure the reverse relationship is maintained
    }

    @OneToOne(mappedBy = "user")
    private Profile profile;




    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
