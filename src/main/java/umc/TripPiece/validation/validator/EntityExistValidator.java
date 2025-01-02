package umc.TripPiece.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.validation.annotation.ExistEntity;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EntityExistValidator implements ConstraintValidator<ExistEntity, Long> {
    private final Map<Class<?>, JpaRepository<?, Long>> repositoryMap;
    private final Map<Class<?>, ErrorStatus> errorStatusMap;

    private Class<?> entityType;
    private String customMsg;

    @Override
    public void initialize(ExistEntity constraintAnnotation){
        this.entityType = constraintAnnotation.entityType();
        this.customMsg = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("ID는 필수 값입니다.").addConstraintViolation();
            return false;
        }

        JpaRepository<?, Long> repository = repositoryMap.get(entityType);
        if (repository == null) {
            throw new IllegalArgumentException("Repository not found for entity type: " + entityType.getName());
        }

        boolean exists = repository.existsById(value);

        if (!exists) {
            context.disableDefaultConstraintViolation();

            // 동적 메시지 설정
            String msg = errorStatusMap.get(entityType).getMessage();
            context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        }

        return exists;
    }



}
