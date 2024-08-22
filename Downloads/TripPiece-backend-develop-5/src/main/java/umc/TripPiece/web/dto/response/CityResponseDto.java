package umc.TripPiece.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CityResponseDto {
    @Getter
    @AllArgsConstructor
    public static class searchDto{
        private String cityName;
        private String countryName;
        private String cityDescription;
        private String countryImage;
        private Long logCount;
    }
}
