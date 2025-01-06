package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.GeneralException;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.converter.TravelConverter;
import umc.TripPiece.domain.Travel;
import umc.TripPiece.domain.TripPiece;
import umc.TripPiece.domain.enums.TravelStatus;
import umc.TripPiece.apiPayload.ApiResponse;
import umc.TripPiece.repository.TravelRepository;
import umc.TripPiece.service.TravelService;
import umc.TripPiece.validation.annotation.ExistEntity;
import umc.TripPiece.validation.annotation.ValidateToken;
import umc.TripPiece.web.dto.request.TravelRequestDto;
import umc.TripPiece.web.dto.response.TravelResponseDto;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag(name = "Travel", description = "여행기 관련 API")
@RestController
@Validated
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;
    private final TravelRepository travelRepository;

    @PostMapping(value = "/mytravels", consumes = "multipart/form-data")
    @Operation(summary = "여행 생성 API", description = "여행 시작하기")
    public ApiResponse<TravelResponseDto.Create> createTravel(@Valid @RequestPart("data") TravelRequestDto.Create request, @RequestPart("thumbnail") MultipartFile thumbnail){
        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new BadRequestHandler(ErrorStatus.MISSING_TRAVEL_THUMBNAIL);
        }
        TravelResponseDto.Create response = travelService.createTravel(request, thumbnail);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/mytravels/end/{travelId}")
    @Operation(summary = "여행 종료 API", description = "여행을 종료하고 요약 정보 반환")
    public ApiResponse<TravelResponseDto.TripSummaryDto> endTravel(@PathVariable("travelId") @ExistEntity(entityType = umc.TripPiece.domain.Travel.class) Long travelId) {
        TravelResponseDto.TripSummaryDto response = travelService.endTravel(travelId);
        return ApiResponse.onSuccess(response);
    }
    @GetMapping("/mytravels/continue/{travelId}")
    @Operation(summary = "여행 이어보기 API", description = "여행을 이어볼 날짜별 조각 반환")
    public ApiResponse<List<TravelResponseDto.TripPieceSummaryDto>> continueTravel(@PathVariable("travelId") @ExistEntity(entityType = umc.TripPiece.domain.Travel.class) Long travelId) {
        List<TravelResponseDto.TripPieceSummaryDto> response = travelService.continueTravel(travelId);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/mytravels/memo/{travelId}")
    @Operation(summary = "메모 기록 API", description = "특정 여행기에서의 여행조각 추가")
    public ApiResponse<TravelResponseDto.CreateTripPieceResultDto> createTripPieceMemo(@RequestBody TravelRequestDto.MemoDto request, @PathVariable("travelId") Long travelId, @RequestHeader("Authorization") String token){

        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));
        if(travel == null) return ApiResponse.onFailure("400", "존재하지 않는 여행기 입니다.", null);
        if(travel.getStatus() == TravelStatus.COMPLETED) return ApiResponse.onFailure("400", "이미 완료된 여행기 입니다.", null);

        if(request.getDescription().length() > 150)
            return ApiResponse.onFailure("400", "글자수 150자 초과 입니다.", null);

        String tokenWithoutBearer = token.substring(7);
        TripPiece tripPiece = travelService.createMemo(travelId, request, tokenWithoutBearer);
        return ApiResponse.onSuccess(TravelConverter.toCreateTripPieceResultDto(tripPiece));
    }

    @PostMapping("/mytravels/emoji/{travelId}")
    @Operation(summary = "이모지 기록 API", description = "특정 여행기에서의 여행조각 추가")
    public ApiResponse<TravelResponseDto.CreateTripPieceResultDto> createTripPieceEmoji(@RequestBody TravelRequestDto.MemoDto request, @PathVariable("travelId") Long travelId, @RequestHeader("Authorization") String token, @RequestParam(name = "emojis") List<String> emojis){

        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));
        if(travel == null) return ApiResponse.onFailure("400", "존재하지 않는 여행기 입니다.", null);
        if(travel.getStatus() == TravelStatus.COMPLETED) return ApiResponse.onFailure("400", "이미 완료된 여행기 입니다.", null);

        if(request.getDescription().length() > 100)
            return ApiResponse.onFailure("400", "글자수 100자 초과 입니다.", null);

        if(emojis.size() != 4)
            return ApiResponse.onFailure("400", "이모지의 갯수는 4개여야 합니다.", null);

        for(String emoji : emojis) {
            Pattern rex = Pattern.compile("[\\x{10000}-\\x{10ffff}\ud800-\udfff]");
            Matcher rexMatcher = rex.matcher(emoji);

            if(!rexMatcher.find())
                return ApiResponse.onFailure("400", "올바른 이모지 형식이 아닙니다.", null);

        }

        String tokenWithoutBearer = token.substring(7);
        TripPiece tripPiece = travelService.createEmoji(travelId, emojis, request, tokenWithoutBearer);
        return ApiResponse.onSuccess(TravelConverter.toCreateTripPieceResultDto(tripPiece));
    }

    @PostMapping(value = "/mytravels/picture/{travelId}", consumes = "multipart/form-data")
    @Operation(summary = "사진 기록 API", description = "특정 여행기에서의 여행조각 추가")
    public ApiResponse<TravelResponseDto.CreateTripPieceResultDto> createTripPiecePicture(@RequestPart("memo") TravelRequestDto.MemoDto request, @PathVariable("travelId") Long travelId, @RequestHeader("Authorization") String token, @RequestPart("photos") List<MultipartFile> photos){

        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));
        if(travel == null) return ApiResponse.onFailure("400", "존재하지 않는 여행기 입니다.", null);
        if(travel.getStatus() == TravelStatus.COMPLETED) return ApiResponse.onFailure("400", "이미 완료된 여행기 입니다.", null);

        if(request.getDescription().length() > 100)
            return ApiResponse.onFailure("400", "글자수 100자 초과 입니다.", null);

        String tokenWithoutBearer = token.substring(7);
        TripPiece tripPiece = travelService.createPicture(travelId, photos, request, tokenWithoutBearer);
        return ApiResponse.onSuccess(TravelConverter.toCreateTripPieceResultDto(tripPiece));
    }

    @PostMapping(value = "/mytravels/selfie/{travelId}", consumes = "multipart/form-data")
    @Operation(summary = "셀카 기록 API", description = "특정 여행기에서의 여행조각 추가")
    public ApiResponse<TravelResponseDto.CreateTripPieceResultDto> createTripPieceSelfie(@Valid @RequestPart("memo") TravelRequestDto.MemoDto request, @PathVariable("travelId") Long travelId, @RequestHeader("Authorization") String token, @RequestPart("photo") MultipartFile photo){

        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));
        if(travel == null) return ApiResponse.onFailure("400", "존재하지 않는 여행기 입니다.", null);
        if(travel.getStatus() == TravelStatus.COMPLETED) return ApiResponse.onFailure("400", "이미 완료된 여행기 입니다.", null);

        if(request.getDescription().length() > 100)
            return ApiResponse.onFailure("400", "글자수 100자 초과 입니다.", null);

        String tokenWithoutBearer = token.substring(7);
        TripPiece tripPiece = travelService.createSelfie(travelId, photo, request, tokenWithoutBearer);
        return ApiResponse.onSuccess(TravelConverter.toCreateTripPieceResultDto(tripPiece));
    }

    @PostMapping(value = "/mytravels/video/{travelId}", consumes = "multipart/form-data")
    @Operation(summary = "비디오 기록 API", description = "특정 여행기에서의 여행조각 추가")
    public ApiResponse<TravelResponseDto.CreateTripPieceResultDto> createTripPieceVideo(@Valid @RequestPart("memo") TravelRequestDto.MemoDto request, @PathVariable("travelId") Long travelId, @RequestHeader("Authorization") String token, @RequestPart("video") MultipartFile video){

        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));
        if(travel == null) return ApiResponse.onFailure("400", "존재하지 않는 여행기 입니다.", null);
        if(travel.getStatus() == TravelStatus.COMPLETED) return ApiResponse.onFailure("400", "이미 완료된 여행기 입니다.", null);

        if(request.getDescription().length() > 100)
            return ApiResponse.onFailure("400", "글자수 100자 초과 입니다.", null);

        String tokenWithoutBearer = token.substring(7);
        TripPiece tripPiece = travelService.createVideo(travelId, video, request, tokenWithoutBearer);
        return ApiResponse.onSuccess(TravelConverter.toCreateTripPieceResultDto(tripPiece));
    }

    @PostMapping(value = "/mytravels/where/{travelId}", consumes = "multipart/form-data")
    @Operation(summary = "'지금 어디에 있나요?' 카테고리 기록 API", description = "특정 여행기에서의 여행조각 추가")
    public ApiResponse<TravelResponseDto.CreateTripPieceResultDto> createTripPieceWhere(@Valid @RequestPart("memo") TravelRequestDto.MemoDto request, @PathVariable("travelId") Long travelId, @RequestHeader("Authorization") String token, @RequestPart("video") MultipartFile video){

        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));
        if(travel == null) return ApiResponse.onFailure("400", "존재하지 않는 여행기 입니다.", null);
        if(travel.getStatus() == TravelStatus.COMPLETED) return ApiResponse.onFailure("400", "이미 완료된 여행기 입니다.", null);

        if(request.getDescription().length() > 100)
            return ApiResponse.onFailure("400", "글자수 100자 초과 입니다.", null);

        String tokenWithoutBearer = token.substring(7);
        TripPiece tripPiece = travelService.createWhere(travelId, video, request, tokenWithoutBearer);
        return ApiResponse.onSuccess(TravelConverter.toCreateTripPieceResultDto(tripPiece));
    }

    @GetMapping("/travels")
    @Operation(summary = "생성된 여행기 API", description = "생성된 여행기 리스트 반환")
    public ResponseEntity<ApiResponse<List<TravelResponseDto.TravelListDto>>> getTravelList(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TravelResponseDto.TravelListDto> travels = travelService.getTravelList(tokenWithoutBearer);

        if (travels.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.onFailure("400", "생성된 여행기 없음.", null), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ApiResponse.onSuccess(travels), HttpStatus.OK);
    }

    @GetMapping("/travels/{travelId}")
    @Operation(summary = "여행기 상세 정보 API", description = "특정 여행기의 상세 정보 반환")
    public ApiResponse<TravelResponseDto.TripSummaryDto> getTravelDetails(@PathVariable("travelId") Long travelId) {
        TravelResponseDto.TripSummaryDto response = travelService.getTravelDetails(travelId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/mytravels")
    @Operation(summary = "현재 진행중인 여행기 반환 API", description = "현재 진행중인 여행기 반환")
    public ApiResponse<TravelResponseDto.getOngoingTravelResultDto> getOngoingTravel(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        TravelResponseDto.getOngoingTravelResultDto travel = travelService.getOngoingTravel(tokenWithoutBearer);
        return ApiResponse.onSuccess(travel);
    }

    @GetMapping("/mytravels/update/{travelId}")
    @Operation(summary = "특정 여행기의 사진들을 모두 불러오는 API", description = "여행기 수정 시에 활용")
    public ApiResponse<List<TravelResponseDto.UpdatablePictureDto>> getUpdatablePictures(@PathVariable("travelId") Long travelId) {
        List<TravelResponseDto.UpdatablePictureDto> response = travelService.getPictureResponses(travelId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/mytravels/thumbnail/{travelId}")
    @Operation(summary = "특정 여행기의 썸네일 사진들을 불러오는 API", description = "9개의 사진을 불러온다")
    public ApiResponse<List<TravelResponseDto.UpdatablePictureDto>> getThumbnailPictures(@PathVariable("travelId") Long travelId) {
        List<TravelResponseDto.UpdatablePictureDto> response = travelService.getThumbnailPictures(travelId);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/mytravels/thumbnail/update/{travelId}")
    @Operation(summary = "특정 여행기에서 썸네일을 편집하는 API", description = "리스트의 크기는 9이며, 썸네일에 표시될 사진의 id가 index 순서대로 정렬되어 있다. id값에 -1이 입력되면 썸네일을 제거한다")
    public ApiResponse<List<TravelResponseDto.UpdatablePictureDto>> updateThumbnailPictures(@PathVariable("travelId") Long travelId, @RequestParam(name = "pictureIdList") List<Long> pictureIdList) {
        List<TravelResponseDto.UpdatablePictureDto> response = travelService.updateThumbnail(travelId, pictureIdList);
        return ApiResponse.onSuccess(response);
    }

}
