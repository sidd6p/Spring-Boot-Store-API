/*
 * UserMapper: MapStruct interface for mapping User entity to UserDto.
 *
 * How it works:
 * 1. MapStruct generates the implementation at build time.
 * 2. The generated UserMapperImpl class converts User objects to UserDto objects.
 * 3. Used in services/controllers for DTO conversion, enabling separation of entity and API models.
 *
 * Flow Diagram:
 *
 *   [User Entity]
 *        |
 *        v
 *   UserMapper.toDto(user)
 *        |
 *        v
 *   [UserDto]
 *
 * MapStruct auto-generates the mapping logic based on field names/types.
 */

package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.RegisterUserRequest;
import com.github.sidd6p.store.dtos.UpdateUserRequest;
import com.github.sidd6p.store.dtos.UserDto;
import com.github.sidd6p.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(source = "user_name", target = "name")
    User toEntity(RegisterUserRequest registerUserRequest);

    @Mapping(source = "user_name", target = "name")
    void updateUser(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}
