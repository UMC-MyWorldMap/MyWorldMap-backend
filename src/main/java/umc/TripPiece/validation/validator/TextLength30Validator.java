package umc.TripPiece.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.TripPiece.payload.code.status.ErrorStatus;
import umc.TripPiece.validation.annotation.CheckEmoji;
import umc.TripPiece.validation.annotation.TextLength30;
import umc.TripPiece.web.dto.request.TravelRequestDto;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class TextLength30Validator implements ConstraintValidator<TextLength30, TravelRequestDto.MemoDto> {

    @Override
    public void initialize(TextLength30 constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TravelRequestDto.MemoDto request, ConstraintValidatorContext context) {
        boolean isValid = request.getDescription().length() <= 30;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.TEXT_LENGTH_30_ERROR.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
