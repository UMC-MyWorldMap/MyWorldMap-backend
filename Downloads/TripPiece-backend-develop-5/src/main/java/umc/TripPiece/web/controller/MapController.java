package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umc.TripPiece.service.TripPieceService;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.ApiResponse;
import umc.TripPiece.web.dto.response.MapResponseDto;
import umc.TripPiece.service.MapService;
import umc.TripPiece.web.dto.response.MapStatsResponseDto;
import umc.TripPiece.web.dto.response.TripPieceResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/maps")
public class MapController {

    @Autowired
    private MapService mapService;

    // 유저별로 맵 리스트를 반환하도록 수정
    @GetMapping("/{userId}")
    @Operation(summary = "유저별 맵 불러오기 API", description = "유저별 맵 리스트 반환")
    public ApiResponse<List<MapResponseDto>> getMapsByUserId(@PathVariable Long userId) {
        List<MapResponseDto> maps = mapService.getMapsByUserId(userId);
        return new ApiResponse<>(true, maps, "Maps for user " + userId + " retrieved successfully");
    }

    // 맵 생성 시 선택한 도시 정보도 함께 처리하도록 수정
    @PostMapping
    @Operation(summary = "맵 생성 API", description = "맵과 도시 정보 생성")
    public ApiResponse<MapResponseDto> createMap(@RequestBody MapRequestDto requestDto) {
        MapResponseDto mapResponseDto = mapService.createMapWithCity(requestDto);
        return new ApiResponse<>(true, mapResponseDto, "Map created successfully with city information");
    }

    // 유저별 나라, 도시 통계 반환
    @GetMapping("/stats/{userId}")
    @Operation(summary = "유저별 맵 통계 API", description = "유저별 방문한 나라와 도시 수 반환")
    public ApiResponse<MapStatsResponseDto> getMapStatsByUserId(@PathVariable Long userId) {
        MapStatsResponseDto stats = mapService.getMapStatsByUserId(userId);
        return new ApiResponse<>(true, stats, "Map stats for user " + userId + " retrieved successfully");
    }


    // 마커 반환 기능 그대로 유지
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

}
