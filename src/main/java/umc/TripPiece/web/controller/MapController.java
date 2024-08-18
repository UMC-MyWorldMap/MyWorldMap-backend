package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umc.TripPiece.service.MapService;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.ApiResponse;
import umc.TripPiece.web.dto.response.MapResponseDto;
import umc.TripPiece.web.dto.response.MapStatsResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/maps")
public class MapController {

    @Autowired
    private MapService mapService;

    @GetMapping("/{userId}")
    @Operation(summary = "유저별 맵 불러오기 API", description = "특정 유저의 맵 리스트 반환")
    public ApiResponse<List<MapResponseDto>> getMapsByUserId(@PathVariable("userId") Long userId) {
        List<MapResponseDto> maps = mapService.getMapsByUserId(userId);
        return new ApiResponse<>(true, maps, "Maps retrieved successfully for userId: " + userId);
    }

    @PostMapping
    @Operation(summary = "맵 생성 API", description = "맵 색깔 지정")
    public ApiResponse<MapResponseDto> createMap(@RequestBody MapRequestDto requestDto) {
        MapResponseDto mapResponseDto = mapService.createMap(requestDto);
        return new ApiResponse<>(true, mapResponseDto, "Map created successfully");
    }

    @GetMapping("/stats/{userId}")
    @Operation(summary = "유저별 방문 나라 수/도시 수 반환 API", description = "특정 유저의 방문한 나라/도시 갯수")
    public ApiResponse<MapStatsResponseDto> getMapStats(@PathVariable("userId") Long userId) {
        MapStatsResponseDto stats = mapService.getMapStatsByUserId(userId);
        return new ApiResponse<>(true, stats, "Map stats retrieved successfully for userId: " + userId);
    }

    @GetMapping("/markers/{userId}")
    @Operation(summary = "구글 지도 위 마커 반환 API", description = "특정 유저의 기록탭의 마커 반환")
    public ApiResponse<List<MapResponseDto.MarkerResponse>> getMarkers(@PathVariable("userId") Long userId) {
        List<MapResponseDto.MarkerResponse> markers = mapService.getMarkersByUserId(userId);

        if (markers == null || markers.isEmpty()) {
            return new ApiResponse<>(false, null, "No markers found for userId: " + userId);
        }
        return new ApiResponse<>(true, markers, "Markers retrieved successfully for userId: " + userId);
    }
}
