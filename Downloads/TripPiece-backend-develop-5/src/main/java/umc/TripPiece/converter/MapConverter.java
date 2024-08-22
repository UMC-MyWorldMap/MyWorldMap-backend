package umc.TripPiece.converter;

import umc.TripPiece.domain.Map;
import umc.TripPiece.domain.City;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.MapResponseDto;

public class MapConverter {

    // MapRequestDto -> Map 변환
    public static Map toMap(MapRequestDto requestDto, City city) {
        return new Map(
                null, // visitId는 자동 생성됨
                requestDto.getUserId(),
                requestDto.getCountryCode(),
                requestDto.getColor(),
                city  // City 필드 추가
        );
    }

    // Map 엔티티 -> MapResponseDto 변환
    public static MapResponseDto toMapResponseDto(Map map) {
        return new MapResponseDto(
                map.getVisitId(),
                map.getUserId(),
                map.getCountryCode(),
                map.getColor()
        );
    }

    // 마커 정보를 반환하는 DTO로 변환
    public static MapResponseDto.getMarkerResponse toMarkerResponseDto(Map map, String markerImg, String countryName, String cityName) {
        return MapResponseDto.getMarkerResponse.builder()
                .color(map.getColor())
                .markerImg(markerImg)
                .countryCode(map.getCountryCode())
                .countryName(countryName)
                .cityName(cityName)
                .build();
    }
}
