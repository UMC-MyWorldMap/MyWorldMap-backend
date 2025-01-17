package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.service.MapService;
import umc.TripPiece.validation.annotation.ExistEntity;
import umc.TripPiece.web.dto.request.MapRequestDto;

import org.springframework.validation.annotation.Validated;

import umc.TripPiece.apiPayload.ApiResponse;

import umc.TripPiece.web.dto.response.MapResponseDto;
import umc.TripPiece.web.dto.response.MapStatsResponseDto;
import umc.TripPiece.web.dto.request.MapColorDto;
import umc.TripPiece.web.dto.request.MapColorsDto;

import java.util.List;

@Tag(name = "Map", description = "지도 관련 API")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MapController {

    private final MapService mapService;
    private final JWTUtil jwtUtil; // JWTUtil 객체 주입

    @GetMapping("/{userId}")
    @Operation(summary = "유저별 맵 불러오기 API", description = "유저별 맵 리스트 반환")
    public ApiResponse<List<MapResponseDto>> getMapsByUserId(@PathVariable(name = "userId") @ExistEntity(entityType = umc.TripPiece.domain.User.class) Long userId) {
        List<MapResponseDto> maps = mapService.getMapsByUserId(userId);
        return ApiResponse.onSuccess(maps);
    }

    @PostMapping
    @Operation(summary = "맵 생성 API", description = "맵과 도시 정보 생성")
    public ApiResponse<MapResponseDto> createMap(@RequestBody @Valid MapRequestDto requestDto) {
        MapResponseDto mapResponseDto = mapService.createMapWithCity(requestDto);
        return ApiResponse.onSuccess(mapResponseDto);
    }

    @GetMapping("/stats/{userId}")
    @Operation(summary = "유저별 맵 통계 API", description = "유저별 방문한 나라와 도시 수 반환")
    public ApiResponse<MapStatsResponseDto> getMapStatsByUserId(@PathVariable(name = "userId") Long userId) {
        MapStatsResponseDto stats = mapService.getMapStatsByUserId(userId);
        return ApiResponse.onSuccess(stats);
    }

    @GetMapping("/markers")
    @Operation(summary = "구글 지도 위 마커 반환 API", description = "나의 기록탭의 마커 반환")
    public ApiResponse<List<MapResponseDto.getMarkerResponse>> getMarkers(@RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.substring(7);
        List<MapResponseDto.getMarkerResponse> markers = mapService.getMarkers(tokenWithoutBearer);
        return ApiResponse.onSuccess(markers);
    }

    @PutMapping("/color/{mapId}")
    @Operation(summary = "맵 색상 수정 API", description = "맵의 색상을 수정")
    public ApiResponse<MapResponseDto> updateMapColor(@PathVariable(name = "mapId") Long mapId, @RequestBody @Valid MapColorDto colorDto) {
        MapResponseDto updatedMap = mapService.updateMapColor(mapId, colorDto.getColor());
        return ApiResponse.onSuccess(updatedMap);
    }

    @DeleteMapping("/color/delete/{mapId}")
    @Operation(summary = "맵 색상 삭제 API", description = "맵의 색상을 삭제")
    public ApiResponse<Void> deleteMapColor(@PathVariable(name = "mapId") Long mapId) {
        mapService.deleteMapColor(mapId);
        return ApiResponse.onSuccess(null);
    }

    @PutMapping("/colors/{mapId}")
    @Operation(summary = "맵 여러 색상 선택 API", description = "맵의 색상을 여러 개 선택")
    public ApiResponse<MapResponseDto> updateMultipleMapColors(@PathVariable(name = "mapId") Long mapId, @RequestBody MapColorsDto colorsDto) {
        MapResponseDto updatedMap = mapService.updateMultipleMapColors(mapId, colorsDto.getColors());
        return ApiResponse.onSuccess(updatedMap);
    }

    @PutMapping("/color")
    @Operation(summary = "맵 색상 수정 (기존 정보 기반)", description = "맵의 색상을 수정 (userId, countryCode, cityId 기반)")
    public ApiResponse<MapResponseDto> updateMapColorWithInfo(@RequestHeader("Authorization") String token,
                                                              @RequestBody @Valid MapRequestDto requestDto) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        MapResponseDto updatedMap = mapService.updateMapColorWithInfo(userId, requestDto.getCountryCode(), requestDto.getCityId(), requestDto.getColor());
        return ApiResponse.onSuccess(updatedMap);
    }

    @DeleteMapping("/color")
    @Operation(summary = "맵 삭제 (기존 정보 기반)", description = "맵을 삭제 (userId, countryCode, cityId 기반)")
    public ApiResponse<Void> deleteMapWithInfo(@RequestHeader("Authorization") String token,
                                               @RequestBody @Valid MapRequestDto requestDto) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        mapService.deleteMapWithInfo(userId, requestDto.getCountryCode(), requestDto.getCityId());
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/visited-countries")
    @Operation(summary = "방문한 나라 누적 API", description = "사용자가 방문한 나라의 리스트와 카운트를 반환")
    public ApiResponse<MapStatsResponseDto> getVisitedCountries(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        MapStatsResponseDto response = mapService.getVisitedCountriesWithProfile(userId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/search")
    @Operation(summary = "도시, 국가 검색 API", description = "도시, 국가 검색")
    public ApiResponse<List<MapResponseDto.searchDto>> searchCities(@RequestParam String keyword) {
        List<MapResponseDto.searchDto> result = mapService.searchCitiesCountry(keyword);
        if (result.isEmpty()) {
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_CITY_COUNTRY);
        } else {
            return ApiResponse.onSuccess(result);
        }
    }
}
