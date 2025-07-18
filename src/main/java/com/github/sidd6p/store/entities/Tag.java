package com.github.sidd6p.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int UNSIGNED")
    private Long id;

    public Tag(String name) {
        this.name = name;
    }

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    private Set<User> users = new HashSet<>();
}
