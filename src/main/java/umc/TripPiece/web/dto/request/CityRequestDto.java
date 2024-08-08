package umc.TripPiece.web.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class CityRequestDto {
    @Getter
    @NoArgsConstructor
    public static class searchDto{
        private String keyword;
    }
}
