package com.asalavei.weathertracker.common;

import java.util.Set;

public class Constants {

    public static final String USER_ATTRIBUTE = "user";
    public static final String LOCATION_ATTRIBUTE = "location";
    public static final String LOCATIONS_ATTRIBUTE = "locations";
    public static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";

    public static final String REDIRECT_HOME = "redirect:/";
    public static final String REDIRECT_SIGNIN = "redirect:/auth/signin";

    public static final String HOME_URL = "/";
    public static final String SIGNIN_URL = "/auth/signin";
    public static final String SIGNUP_URL = "/auth/signup";
    public static final String REDIRECT_TO_PARAM = "redirect_to";
    public static final String SIGNIN_URL_WITH_REDIRECT = SIGNIN_URL + "?" + REDIRECT_TO_PARAM + "=";

    public static final String SIGNIN_VIEW = "auth/signin";
    public static final String SIGNUP_VIEW = "auth/signup";
    public static final String HOME_VIEW = "home";
    public static final String LOCATIONS_VIEW = "locations";
    public static final String ERROR_404_VIEW = "error/404";
    public static final String ERROR_500_VIEW = "error/500";

    public static final Set<String> AUTH_PAGES = Set.of(SIGNIN_URL, SIGNUP_URL);

    private Constants() {
    }
}
