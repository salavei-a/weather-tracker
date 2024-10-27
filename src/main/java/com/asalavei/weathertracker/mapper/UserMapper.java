package com.asalavei.weathertracker.mapper;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.dto.SignInResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    SignInResponseDto toDto(User entity);
}
