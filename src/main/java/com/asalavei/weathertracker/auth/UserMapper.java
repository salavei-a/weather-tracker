package com.asalavei.weathertracker.auth;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    SignInResponseDto toDto(User entity);
}
