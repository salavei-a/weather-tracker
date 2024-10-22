package com.asalavei.weathertracker.mapper;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.dto.UserResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User entity);
}
