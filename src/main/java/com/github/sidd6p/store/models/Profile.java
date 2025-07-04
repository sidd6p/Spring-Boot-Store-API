package com.github.sidd6p.store.models;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bio")
    private String bio;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private java.sql.Date dateOfBirth;

    @OneToOne()
    @JoinColumn(name = "id")
    @MapsId
    @ToString.Exclude
    private User user;

    // columnDefinition specifies the exact SQL fragment used when generating the DDL for this column.
    // Here, it ensures the loyalty_points column is UNSIGNED and defaults to 0 in the database.
    @Column(name = "loyalty_points", columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer loyaltyPoints = 0;

}
