package umc.TripPiece.converter;

import umc.TripPiece.domain.Map;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.MapResponseDto;

public class MapConverter {

    public static Map toMap(MapRequestDto requestDto) {
        return new Map(
                null,
                requestDto.getUserId(),
                requestDto.getCountryCode(),
                requestDto.getColor()
        );
    }

    public static MapResponseDto toMapResponseDto(Map map) {
        return new MapResponseDto(
                map.getVisitId(),
                map.getUserId(),
                map.getCountryCode(),
                map.getColor()
        );
    }

    // MarkerResponseDto를 반환하는 메서드
    public static MapResponseDto.MarkerResponse toMarkerResponseDto(Map map, String thumbnailUrl, String countryName, String cityName) {
        return new MapResponseDto.MarkerResponse(
                countryName,
                cityName,
                thumbnailUrl
        );
    }
}
