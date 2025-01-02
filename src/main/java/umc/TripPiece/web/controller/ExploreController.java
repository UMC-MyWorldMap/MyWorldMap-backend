package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.TripPiece.apiPayload.ApiResponse;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.service.ExploreService;
import umc.TripPiece.web.dto.response.ExploreResponseDto;
import umc.TripPiece.web.dto.response.TravelResponseDto;

import java.util.List;

@Tag(name = "Explore", description = "탐색 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/explore")
public class ExploreController {

    private final ExploreService exploreService;
    @GetMapping("/search")
    @Operation(summary = "도시, 국가 검색 API", description = "도시, 국가 검색")
    public ApiResponse<List<ExploreResponseDto.ExploreListDto>> getSearchedTravelList(@RequestParam String query, @RequestParam(defaultValue = "latest") String sort) {
        List<ExploreResponseDto.ExploreListDto> travels;
        if ("latest".equals(sort) || "oldest".equals(sort)) {
            travels = exploreService.searchTravels(query, sort);
        } else {
            throw new BadRequestHandler(ErrorStatus.INVALID_TRAVEL_PARARM);
        }
     if(travels.isEmpty()){
         throw new NotFoundHandler(ErrorStatus.NOT_FOUND_TRAVEL);
     }
        return ApiResponse.onSuccess(travels);
    }

    @GetMapping("/popular")
    @Operation(summary = "요즘 떠오르는 도시 API", description = "도시별 여행기순 내림차순")
    public ApiResponse<List<ExploreResponseDto.PopularCitiesDto>> getPopularCities(){
        List<ExploreResponseDto.PopularCitiesDto> cities = exploreService.getCitiesByTravelCount();
        return ApiResponse.onSuccess(cities);
    }
}
