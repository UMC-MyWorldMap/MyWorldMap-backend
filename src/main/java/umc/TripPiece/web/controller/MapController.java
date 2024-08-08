package umc.TripPiece.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.MapResponseDto;
import umc.TripPiece.web.dto.response.ApiResponse;
import umc.TripPiece.web.dto.response.MapStatsResponseDto;
import umc.TripPiece.service.MapService;

import java.util.List;

@RestController
@RequestMapping("/api/maps")
public class MapController {

    @Autowired
    private MapService mapService;

    @GetMapping
    public ApiResponse<List<MapResponseDto>> getAllMaps() {
        List<MapResponseDto> maps = mapService.getAllMaps();
        return new ApiResponse<>(true, maps, "All maps retrieved successfully");
    }

    @PostMapping
    public ApiResponse<MapResponseDto> createMap(@RequestBody MapRequestDto requestDto) {
        MapResponseDto mapResponseDto = mapService.createMap(requestDto);
        return new ApiResponse<>(true, mapResponseDto, "Map created successfully");
    }

    @GetMapping("/stats/{userId}")
    public ApiResponse<MapStatsResponseDto> getMapStats(@PathVariable Long userId) {
        MapStatsResponseDto stats = mapService.getMapStats(userId);
        return new ApiResponse<>(true, stats, "Map stats retrieved successfully");
    }
}
