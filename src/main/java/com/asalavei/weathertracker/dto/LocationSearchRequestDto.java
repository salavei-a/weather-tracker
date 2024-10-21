package com.asalavei.weathertracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationSearchRequestDto {

    @NotBlank
    @Size(min = 1, max = 60)
    private String name;
}
