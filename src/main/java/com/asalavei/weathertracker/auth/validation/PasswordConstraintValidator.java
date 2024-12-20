package com.asalavei.weathertracker.auth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        final PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(1, 30),
                new WhitespaceRule()));

        final RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        validator.getMessages(result).forEach(
                message -> context.buildConstraintViolationWithTemplate(
                                message.endsWith(".") ? message.substring(0, message.length() - 1) : message
                        )
                        .addConstraintViolation()
        );

        return false;
    }
}
