package umc.TripPiece.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.TripPiece.validation.validator.CheckEmojiNumValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckEmojiNumValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckEmojiNum {

    String message() default "이모지의 갯수는 4개여야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}