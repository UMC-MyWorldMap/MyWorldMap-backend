package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.TripPiece.payload.ApiResponse;
import umc.TripPiece.service.CityService;
import umc.TripPiece.web.dto.request.CityRequestDto;
import umc.TripPiece.web.dto.response.CityResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @PostMapping("search/cities")
    @Operation(
            summary = "도시, 국가 검색 API",
            description = "도시, 국가 검색"

    )
    public ResponseEntity<ApiResponse<List<CityResponseDto.searchDto>>> searchCities(@RequestBody @Valid CityRequestDto.searchDto request){
        List<CityResponseDto.searchDto> result = cityService.searchCity(request);

        if (result.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.onFailure("400", "No matching cities or countries found.", null), HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(ApiResponse.onSuccess(result), HttpStatus.OK);
        }
    }
}