package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dto.CurrentWeatherDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;
import com.asalavei.weathertracker.exception.WeatherServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.web.client.RestClient.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OpenWeatherService.class, OpenWeatherServiceTest.TestConfig.class})
class OpenWeatherServiceTest {

    private static final String LOCATION_NAME = "Minsk";

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private OpenWeatherService openWeatherService;

    @Test
    void givenCorrectLocationName_whenFetchLocationDetails_thenReturnLocationList() throws JsonProcessingException {
        String responseBody = """
                [
                       {
                           "name": "Minsk",
                           "local_names": {
                               "ia": "Minsk",
                               "oc": "Minsk",
                               "hy": "Մինսկ",
                               "et": "Minsk",
                               "kn": "ಮಿನ್ಸ್ಕ್",
                               "hi": "मिन्‍स्‍क",
                               "sv": "Minsk",
                               "en": "Minsk",
                               "bg": "Минск",
                               "hu": "Minszk",
                               "tg": "Минск",
                               "he": "מינסק",
                               "gl": "Minsk",
                               "vo": "Minsk",
                               "ko": "민스크",
                               "pt": "Minsk",
                               "lt": "Minskas",
                               "ky": "Минск",
                               "nl": "Minsk",
                               "fr": "Minsk",
                               "vi": "Minxcơ",
                               "ru": "Минск",
                               "fa": "مینسک",
                               "fi": "Minsk",
                               "mk": "Минск",
                               "ka": "მინსკი",
                               "feature_name": "Minsk",
                               "cu": "Мѣньскъ",
                               "es": "Minsk",
                               "el": "Μινσκ",
                               "la": "Minscum",
                               "th": "มินสก์",
                               "ug": "مىنىسكى",
                               "ga": "Minsc",
                               "bo": "མིན་སིཀ།",
                               "mr": "मिन्‍स्‍क",
                               "yi": "מינסק",
                               "ku": "Mînsk",
                               "lv": "Minska",
                               "ascii": "Minsk",
                               "ja": "ミンスク",
                               "it": "Minsk",
                               "kk": "Минск",
                               "no": "Minsk",
                               "cv": "Минск",
                               "zh": "明斯克",
                               "ar": "مينسك",
                               "kv": "Минск",
                               "de": "Minsk",
                               "cs": "Minsk",
                               "eo": "Minsko",
                               "io": "Minsk",
                               "hr": "Minsk",
                               "sl": "Minsk",
                               "pl": "Mińsk",
                               "ta": "மின்ஸ்க்",
                               "is": "Minsk",
                               "ml": "മിൻസ്ക്",
                               "sk": "Minsk",
                               "tt": "Минск",
                               "uk": "Мінськ",
                               "sr": "Минск",
                               "os": "Минск",
                               "ur": "منسک",
                               "be": "Мінск"
                           },
                           "lat": 53.9024716,
                           "lon": 27.5618225,
                           "country": "BY"
                       },
                       {
                           "name": "Минск",
                           "lat": 57.099061,
                           "lon": 93.3343983,
                           "country": "RU",
                           "state": "Krasnoyarsk Krai"
                       }
                   ]
                """;

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        RequestBodySpec requestBodySpec = Mockito.mock(RequestBodySpec.class);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        List<LocationResponseDto> mockResponse = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        List<LocationResponseDto> locations = weatherService.fetchLocationDetails(LOCATION_NAME);

        assertFalse(locations.isEmpty());
        assertTrue(locations.stream().anyMatch(l -> l.getName().equals(LOCATION_NAME)));
    }

    @Test
    void givenIncorrectLocationName_whenFetchLocationDetails_thenReturnEmptyList() throws JsonProcessingException {
        String incorrectLocationName = "Minskk";

        String responseBody = "[]";
        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        RequestBodySpec requestBodySpec = Mockito.mock(RequestBodySpec.class);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        List<LocationResponseDto> mockResponse = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        List<LocationResponseDto> locations = weatherService.fetchLocationDetails(incorrectLocationName);

        assertTrue(locations.isEmpty());
    }

    @Test
    void givenInvalidApiKey_whenFetchLocationDetails_thenThrowWeatherServiceException() {
        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        RequestBodySpec requestBodySpec = Mockito.mock(RequestBodySpec.class);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenThrow(WeatherServiceException.class);

        assertThrows(WeatherServiceException.class, () -> weatherService.fetchLocationDetails(LOCATION_NAME));
    }

    @Test
    void givenCorrectCoordinates_whenFetchWeatherByCoordinates_thenReturnCurrentWeatherDto() throws JsonProcessingException {
        BigDecimal latitude = new BigDecimal("41.650950");
        BigDecimal longitude = new BigDecimal("41.636009");
        String responseBody = """
                {
                     "coord": {
                         "lon": 41.636,
                         "lat": 41.651
                     },
                     "weather": [
                         {
                             "id": 803,
                             "main": "Clouds",
                             "description": "broken clouds",
                             "icon": "04n"
                         }
                     ],
                     "base": "stations",
                     "main": {
                         "temp": 13.97,
                         "feels_like": 13.56,
                         "temp_min": 13.97,
                         "temp_max": 13.97,
                         "pressure": 1025,
                         "humidity": 82,
                         "sea_level": 1025,
                         "grnd_level": 1015
                     },
                     "visibility": 10000,
                     "wind": {
                         "speed": 0.51,
                         "deg": 0
                     },
                     "clouds": {
                         "all": 75
                     },
                     "dt": 1729866925,
                     "sys": {
                         "type": 1,
                         "id": 8858,
                         "country": "GE",
                         "sunrise": 1729827427,
                         "sunset": 1729865858
                     },
                     "timezone": 14400,
                     "id": 615532,
                     "name": "Batumi",
                     "cod": 200
                 }
                """;

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        RequestBodySpec requestBodySpec = Mockito.mock(RequestBodySpec.class);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        CurrentWeatherDto mockResponse = objectMapper.readValue(responseBody, CurrentWeatherDto.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.body(CurrentWeatherDto.class)).thenReturn(mockResponse);

        CurrentWeatherDto currentWeatherDto = weatherService.fetchWeatherByCoordinates(latitude, longitude);

        assertEquals("Batumi", currentWeatherDto.getName());
        assertEquals(new BigDecimal("13.97"), currentWeatherDto.getTemperatureInfo().getTemperature());
    }

    @Test
    void givenIncorrectCoordinates_whenFetchWeatherByCoordinates_thenThrowWeatherServiceException() {
        BigDecimal latitude = new BigDecimal("99");
        BigDecimal longitude = new BigDecimal("99");

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        RequestBodySpec requestBodySpec = Mockito.mock(RequestBodySpec.class);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(Class.class))).thenThrow(WeatherServiceException.class);

        assertThrows(WeatherServiceException.class, () -> openWeatherService.fetchWeatherByCoordinates(latitude, longitude));
    }

    @Configuration
    public static class TestConfig {

        @Bean
        public RestClient restClient() {
            return Mockito.mock(RestClient.class);
        }

        @Bean
        public SessionService sessionService() {
            return Mockito.mock(SessionService.class);
        }

        @Bean
        public LocationService locationService() {
            return Mockito.mock(LocationService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}