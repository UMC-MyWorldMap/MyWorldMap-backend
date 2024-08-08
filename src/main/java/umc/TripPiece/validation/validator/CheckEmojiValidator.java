package umc.TripPiece.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.TripPiece.payload.code.status.ErrorStatus;
import umc.TripPiece.validation.annotation.CheckEmoji;
import umc.TripPiece.validation.annotation.CheckEmojiNum;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CheckEmojiValidator implements ConstraintValidator<CheckEmoji, List<String>> {

    @Override
    public void initialize(CheckEmoji constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> emojis, ConstraintValidatorContext context) {
        boolean isValid = true;

        for(String emoji : emojis) {
            Pattern rex = Pattern.compile("[\\x{10000}-\\x{10ffff}\ud800-\udfff]");
            Matcher rexMatcher = rex.matcher(emoji);

            if(!rexMatcher.find()) {
                isValid = false;
                break;
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.EMOJI_NUMBER_ERROR.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
