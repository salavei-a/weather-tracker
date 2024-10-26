package com.asalavei.weathertracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserRequestDto {

    @Pattern(regexp = "^\\s*[_.@A-Za-z0-9-]*\\s*$",
             message = "Username can only contain letters, digits, dots (.), underscores (_), at signs (@), and hyphens (-)")
    @NotBlank(message = "Username is required")
    @Size(min = 1, max = 39, message = "Username must be between 1 and 39 characters long ")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 50, message = "Password must be between 1 and 50 characters long")
    private String password;
}
