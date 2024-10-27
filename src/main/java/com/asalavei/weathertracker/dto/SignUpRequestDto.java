package com.asalavei.weathertracker.dto;

import com.asalavei.weathertracker.validation.PasswordMatches;
import com.asalavei.weathertracker.validation.ValidPassword;
import com.asalavei.weathertracker.validation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@PasswordMatches
public class SignUpRequestDto {

    @ValidUsername
    @NotBlank(message = "Username is required")
    @Size(min = 1, max = 39, message = "Username must be between 1 and 39 characters long")
    private String username;

    @ValidPassword
    private String password;

    @NotNull
    @Size(min = 1)
    private String matchingPassword;
}
