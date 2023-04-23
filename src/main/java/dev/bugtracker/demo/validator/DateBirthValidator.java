package dev.bugtracker.demo.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateBirthValidator implements ConstraintValidator<DateBirthConstraint, LocalDate> {
    @Override
    public void initialize(DateBirthConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isBefore(LocalDate.now().minusYears(18));
    }
}
