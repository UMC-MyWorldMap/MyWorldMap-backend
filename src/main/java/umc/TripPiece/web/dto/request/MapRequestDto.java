package umc.TripPiece.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import umc.TripPiece.domain.enums.Color;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapRequestDto {

    private Long userId;

    @NotBlank
    private String countryCode;

    @NotBlank
    private String color; // 변경: String 타입으로 수정

    private Long cityId;
}