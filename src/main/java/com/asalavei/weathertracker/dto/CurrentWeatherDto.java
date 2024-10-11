package com.asalavei.weathertracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeatherDto {

    private String name;

    @JsonProperty("weather")
    private List<Condition> conditions;

    @JsonProperty("main")
    private TemperatureInfo temperatureInfo;

    @JsonProperty("sys")
    private LocationInfo locationInfo;

    @Data
    private static class Condition {

        private String main;

        private String description;

        private String icon;
    }

    @Data
    public static class TemperatureInfo {

        @JsonProperty("temp")
        private BigDecimal temperature;

        @JsonProperty("feels_like")
        private BigDecimal feelsLike;
    }

    @Data
    public static class LocationInfo {

        private String country;
    }
}