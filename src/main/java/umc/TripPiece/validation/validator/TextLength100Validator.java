package umc.TripPiece.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.TripPiece.payload.code.status.ErrorStatus;
import umc.TripPiece.validation.annotation.TextLength100;
import umc.TripPiece.validation.annotation.TextLength30;
import umc.TripPiece.web.dto.request.TravelRequestDto;

@Component
@RequiredArgsConstructor
public class TextLength100Validator implements ConstraintValidator<TextLength100, TravelRequestDto.MemoDto> {

    @Override
    public void initialize(TextLength100 constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TravelRequestDto.MemoDto request, ConstraintValidatorContext context) {
        boolean isValid = request.getDescription().length() <= 100;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.TEXT_LENGTH_100_ERROR.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
