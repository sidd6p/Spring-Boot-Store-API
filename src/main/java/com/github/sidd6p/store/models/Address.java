package com.github.sidd6p.store.models;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", street='" + street + '\'' +
                ", userId=" + userId +
                '}';
    }
}
