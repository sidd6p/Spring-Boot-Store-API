package com.github.sidd6p.store.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int UNSIGNED")
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private Set<Product> products = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }

}