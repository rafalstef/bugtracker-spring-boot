package dev.bugtracker.demo.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateBirthValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateBirthConstraint {
    String message() default "The minimum age requirement is 18";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
