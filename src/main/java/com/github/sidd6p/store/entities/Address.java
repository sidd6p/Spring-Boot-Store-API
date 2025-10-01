package com.github.sidd6p.store.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Setter
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "zip", nullable = false)
    private String zip;

    @Column(name = "street", nullable = false)
    private String street;


    // Lombok's @ToString generates a toString() method including all fields by default.
    // We use @ToString.Exclude on the 'user' field to prevent potential infinite recursion
    // when Address and User reference each other (bi-directional relationship).
    // This avoids stack overflow errors and keeps the toString() output concise.
    @ManyToOne()
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

}
