package com.asalavei.weathertracker.mapper;

import com.asalavei.weathertracker.dbaccess.entity.User;
import com.asalavei.weathertracker.dto.UserResponseDto;
import com.asalavei.weathertracker.dto.UserRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User entity);
}
