package com.asalavei.weathertracker.weather.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationSearchRequestDto {

    @NotBlank
    @Size(min = 1, max = 60)
    private String name;
}
