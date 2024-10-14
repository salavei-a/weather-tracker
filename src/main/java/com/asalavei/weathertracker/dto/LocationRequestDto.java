package com.asalavei.weathertracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationRequestDto {

    @NotBlank
    private String name;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
