package umc.TripPiece.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import umc.TripPiece.validation.annotation.ValidBirth;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FutureDateValidator implements ConstraintValidator<ValidBirth, String> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false; // 생일 값이 비어있는 경우
        }

        try {
            // 날짜 형식 검증
            LocalDate birthDate = LocalDate.parse(value, DATE_FORMATTER);

            // 현재 날짜와 비교
            return !birthDate.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false; // 형식이 올바르지 않은 경우
        }
    }
}
