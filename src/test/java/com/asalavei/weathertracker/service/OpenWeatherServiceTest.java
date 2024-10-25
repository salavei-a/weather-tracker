package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dto.LocationResponseDto;
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
    private static final String RESPONSE_BODY = """
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

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WeatherService weatherService;

    @Test
    void givenValidLocationName_whenFetchLocationDetails_thenReturnLocationList() throws JsonProcessingException {
        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        RequestBodySpec requestBodySpec = Mockito.mock(RequestBodySpec.class);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);

        List<LocationResponseDto> mockResponse = objectMapper.readValue(RESPONSE_BODY, new TypeReference<>() {
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