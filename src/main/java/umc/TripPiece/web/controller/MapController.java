package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import umc.TripPiece.service.CityService;
import umc.TripPiece.service.MapService;
import umc.TripPiece.web.dto.request.CityRequestDto;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.ApiResponse;
import umc.TripPiece.web.dto.response.CityResponseDto;
import umc.TripPiece.web.dto.response.MapResponseDto;
import umc.TripPiece.web.dto.response.MapStatsResponseDto;
import umc.TripPiece.web.dto.request.MapColorDto;
import umc.TripPiece.web.dto.request.MapColorsDto;

import java.util.List;

@Tag(name = "Map", description = "지도 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MapController {

    private final CityService cityService;
    @Autowired
    private MapService mapService;

    @GetMapping("/{userId}")
    @Operation(summary = "유저별 맵 불러오기 API", description = "유저별 맵 리스트 반환")
    public ApiResponse<List<MapResponseDto>> getMapsByUserId(@PathVariable Long userId) {
        List<MapResponseDto> maps = mapService.getMapsByUserId(userId);
        return new ApiResponse<>(true, maps, "Maps for user " + userId + " retrieved successfully");
    }

    @PostMapping
    @Operation(summary = "맵 생성 API", description = "맵과 도시 정보 생성")
    public ApiResponse<MapResponseDto> createMap(@RequestBody MapRequestDto requestDto) {
        MapResponseDto mapResponseDto = mapService.createMapWithCity(requestDto);
        return new ApiResponse<>(true, mapResponseDto, "Map created successfully with city information");
    }

    @GetMapping("/stats/{userId}")
    @Operation(summary = "유저별 맵 통계 API", description = "유저별 방문한 나라와 도시 수 반환")
    public ApiResponse<MapStatsResponseDto> getMapStatsByUserId(@PathVariable Long userId) {
        MapStatsResponseDto stats = mapService.getMapStatsByUserId(userId);
        return new ApiResponse<>(true, stats, "Map stats for user " + userId + " retrieved successfully");
    }

    @GetMapping("/markers")
    @Operation(summary = "구글 지도 위 마커 반환 API", description = "나의 기록탭의 마커 반환")
    public ApiResponse<List<MapResponseDto.getMarkerResponse>> getMarkers(@RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.substring(7);
        List<MapResponseDto.getMarkerResponse> markers = mapService.getMarkers(tokenWithoutBearer);

        if (markers == null || markers.isEmpty()) {
            return new ApiResponse<>(false, null, "No markers found");
        }
        return new ApiResponse<>(true, markers, "Markers retrieved successfully");
    }

    // 맵 색상 수정 엔드포인트
    @PutMapping("/{mapId}/color")
    @Operation(summary = "맵 색상 수정 API", description = "맵의 색상을 수정")
    public ApiResponse<MapResponseDto> updateMapColor(@PathVariable Long mapId, @RequestBody MapColorDto colorDto) {
        MapResponseDto updatedMap = mapService.updateMapColor(mapId, colorDto.getColor());
        return new ApiResponse<>(true, updatedMap, "Map color updated successfully");
    }

    // 맵 색상 삭제 엔드포인트
    @DeleteMapping("/{mapId}/color")
    @Operation(summary = "맵 색상 삭제 API", description = "맵의 색상을 삭제")
    public ApiResponse<Void> deleteMapColor(@PathVariable Long mapId) {
        mapService.deleteMapColor(mapId);
        return new ApiResponse<>(true, null, "Map color deleted successfully");
    }

    // 여러 색상 선택 엔드포인트
    @PutMapping("/{mapId}/colors")
    @Operation(summary = "맵 여러 색상 선택 API", description = "맵의 색상을 여러 개 선택")
    public ApiResponse<MapResponseDto> updateMultipleMapColors(@PathVariable Long mapId, @RequestBody MapColorsDto colorsDto) {
        MapResponseDto updatedMap = mapService.updateMultipleMapColors(mapId, colorsDto.getColors());
        return new ApiResponse<>(true, updatedMap, "Map colors updated successfully");
    }

    @PostMapping("/search")
    @Operation(summary = "도시, 국가 검색 API", description = "도시, 국가 검색")
    public ResponseEntity<umc.TripPiece.payload.ApiResponse<List<CityResponseDto.searchDto>>> searchCities(@RequestBody @Valid CityRequestDto.searchDto request){
        List<CityResponseDto.searchDto> result = cityService.searchCity(request);

        if (result.isEmpty()) {
            return new ResponseEntity<>(umc.TripPiece.payload.ApiResponse.onFailure("400", "No matching cities or countries found.", null), HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(umc.TripPiece.payload.ApiResponse.onSuccess(result), HttpStatus.OK);
        }
    }
}
