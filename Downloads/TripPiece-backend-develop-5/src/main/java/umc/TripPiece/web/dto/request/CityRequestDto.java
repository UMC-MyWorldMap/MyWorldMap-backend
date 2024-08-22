package umc.TripPiece.web.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class CityRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class searchDto{
        @NotNull(message = "Keyword cannot be null.")
        private String keyword;
    }
}
