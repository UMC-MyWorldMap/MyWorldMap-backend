package umc.TripPiece.converter;

import umc.TripPiece.domain.Map;
import umc.TripPiece.domain.City;
import umc.TripPiece.domain.enums.Color;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.MapResponseDto;

import java.util.Collections;

public class MapConverter {

    // MapRequestDto -> Map 변환
    public static Map toMap(MapRequestDto requestDto, City city) {
        return new Map(
                null,
                requestDto.getUserId(),
                requestDto.getCountryCode(),
                requestDto.getColor(), // String 값 그대로 설정
                Collections.emptyList(),
                city
        );
    }

    // Map 엔티티 -> MapResponseDto 변환
    public static MapResponseDto toMapResponseDto(Map map) {
        return new MapResponseDto(
                map.getVisitId(),
                map.getUserId(),
                map.getCountryCode(),
                map.getColor() // String 값 반환
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

    // 도시 정보를 검색 DTO로 변환
    public static MapResponseDto.searchDto toSearchDto(City city) {
        return new MapResponseDto.searchDto(
                city.getName(),
                city.getCountry().getName(),
                city.getComment(),
                city.getCountry().getCountryImage(),
                city.getLogCount(),
                city.getId()
        );
    }
}