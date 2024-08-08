package umc.TripPiece.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.TripPiece.payload.code.status.ErrorStatus;
import umc.TripPiece.validation.annotation.CheckEmojiNum;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckEmojiNumValidator implements ConstraintValidator<CheckEmojiNum, List<String>> {

    @Override
    public void initialize(CheckEmojiNum constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> emojis, ConstraintValidatorContext context) {
        boolean isValid = emojis.size() == 4;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.EMOJI_NUMBER_ERROR.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
