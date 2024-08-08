package umc.TripPiece.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.TripPiece.validation.validator.CheckEmojiNumValidator;
import umc.TripPiece.validation.validator.CheckEmojiValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckEmojiValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckEmoji {

    String message() default "비정상적인 입력: 이모지가 아닙니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}