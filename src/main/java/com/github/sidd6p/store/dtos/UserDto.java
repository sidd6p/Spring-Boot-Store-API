package com.github.sidd6p.store.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {

    @JsonIgnore
    private long id;

    @JsonProperty("user_name")
    private String name;
    private  String email;
}
