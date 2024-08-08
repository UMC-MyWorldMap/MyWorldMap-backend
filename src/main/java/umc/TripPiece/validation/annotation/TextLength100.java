package umc.TripPiece.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.TripPiece.validation.validator.CheckEmojiValidator;
import umc.TripPiece.validation.validator.TextLength100Validator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TextLength100Validator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface TextLength100 {

    String message() default "글자 수 100자 초과 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}