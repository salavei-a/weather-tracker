package com.asalavei.weathertracker.auth.validation;

import com.asalavei.weathertracker.auth.SignUpRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext constraintValidatorContext) {
        final SignUpRequestDto signUpDto = (SignUpRequestDto) obj;
        return signUpDto.getPassword().equals(signUpDto.getMatchingPassword());
    }
}
