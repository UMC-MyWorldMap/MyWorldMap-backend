package umc.TripPiece.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.TripPiece.validation.validator.EntityExistValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EntityExistValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistEntity {
    String message() default "";
    Class<?> entityType();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
