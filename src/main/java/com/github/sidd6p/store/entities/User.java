package com.github.sidd6p.store.entities;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    // CascadeType.PERSIST: When saving a User, also save its Addresses.
    // CascadeType.REMOVE: When deleting a User, also delete its Addresses.
    // orphanRemoval = true: When removing an Address from the addresses list, delete it from the database even if the User is not deleted.
    // fetch = FetchType.LAZY: Only load addresses when explicitly accessed, preventing N+1 queries
    @Builder.Default // Lombok: initializes the addresses list to an empty ArrayList by default
    @JsonManagedReference
    private List<Address> addresses = new ArrayList<>();

    public void addAddress(Address address) {
        addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setUser(null); // Clear the user reference in the address
    }


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_tags", // Join table name
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key column for User
            inverseJoinColumns = @JoinColumn(name = "tag_id") // Foreign key column for Tag
    )
    @Builder.Default
    @JsonManagedReference
    private Set<Tag> tags = new HashSet<>();

   public void addTag(Tag tag) {
        tags.add(tag);
        tag.getUsers().add(this); // Ensure the reverse relationship is maintained
    }
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getUsers().remove(this); // Ensure the reverse relationship is maintained
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonManagedReference
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

/*
 * JSON Serialization and Circular Reference Handling:
 *
 * This User entity has bidirectional relationships with Address, Tag, and Profile entities.
 * When Spring Boot REST controllers serialize these entities to JSON (via Jackson),
 * circular references can cause infinite loops and stack overflow errors.
 *
 * For example: User → Tag → User → Tag → User... (infinite nesting)
 *
 * Solution: Jackson annotations to break circular references:
 *
 * @JsonManagedReference - Applied to the "owning" side of relationships in User entity
 *   - On addresses: User will include Address data in JSON, but Address won't include User back
 *   - On tags: User will include Tag data in JSON, but Tag won't include User back
 *   - On profile: User will include Profile data in JSON, but Profile won't include User back
 *
 * @JsonBackReference - Applied to the "inverse" side in related entities (Address, Tag, Profile)
 *   - These fields are ignored during JSON serialization to prevent infinite loops
 *   - The JPA relationships remain fully functional for database operations
 *
 * Result: Clean JSON output with complete relationship data but no circular references
 * Example JSON structure:
 * {
 *   "id": 1,
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "addresses": [{"id": 1, "city": "NYC", "street": "Main St"}],
 *   "tags": [{"id": 1, "name": "VIP"}],
 *   "profile": {"id": 1, "bio": "Software Developer", "loyaltyPoints": 100}
 * }
 *
 * Note: This only affects JSON serialization. All JPA operations (queries, cascading,
 * relationship management) work normally in both directions.
 */
