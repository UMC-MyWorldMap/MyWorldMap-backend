package umc.TripPiece.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.TripPiece.validation.validator.CheckEmojiValidator;
import umc.TripPiece.validation.validator.TextLength30Validator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TextLength30Validator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface TextLength30 {

    String message() default "글자 수 30자 초과 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}