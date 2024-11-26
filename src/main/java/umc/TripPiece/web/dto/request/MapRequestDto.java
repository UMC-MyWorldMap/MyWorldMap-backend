package umc.TripPiece.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import umc.TripPiece.domain.enums.Color;
import umc.TripPiece.validation.annotation.ExistCity;
import umc.TripPiece.validation.annotation.ExistUser;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapRequestDto {

    @ExistUser
    private Long userId;

    @NotBlank
    private String countryCode;

    @NotNull
    private Color color;

    @ExistCity
    private Long cityId; // 추가: 선택한 도시 ID
}
