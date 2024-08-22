package umc.TripPiece.web.dto.response;

import lombok.*;
import umc.TripPiece.domain.enums.Color;
import umc.TripPiece.domain.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapResponseDto {
    private Long visitId;
    private Long userId;
    private String countryCode;
    private Color color;

    public MapResponseDto(Map map) {
        this.visitId = map.getVisitId();
        this.userId = map.getUserId();
        this.countryCode = map.getCountryCode();
        this.color = map.getColor();
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class getMarkerResponse {
        private Color color;
        private String markerImg;
        private String countryCode;
        private String countryName;
        private String cityName;
    }
}