package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.apiPayload.ApiResponse;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.domain.TripPiece;
import umc.TripPiece.repository.TripPieceRepository;
import umc.TripPiece.service.TripPieceService;
import umc.TripPiece.validation.annotation.ExistEntity;
import umc.TripPiece.web.dto.request.TripPieceRequestDto;
import umc.TripPiece.web.dto.response.TripPieceResponseDto;

import java.util.List;

@Tag(name = "TripPiece", description = "여행 조각 관련 API")
@RestController
@RequiredArgsConstructor
public class TripPieceController {

    private final TripPieceService tripPieceService;
    private final TripPieceRepository tripPieceRepository;

    @GetMapping("/mytrippieces/all")
    @Operation(summary = "지난 여행 조각(전체) API", description = "유저의 여행조각(전체)을 정렬 기준에 따라 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getTripPieceList(
            @RequestParam(value = "sort", defaultValue = "latest") String sort) {

        if (!"latest".equals(sort) && !"earliest".equals(sort)) {
            throw new BadRequestHandler(ErrorStatus.INVALID_TRIPPIECE_SORT_OPTION);
        }

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getTripPieceList(sort);

        if (tripPieceList == null || tripPieceList.isEmpty()) {
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE);
        }
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/memo")
    @Operation(summary = "지난 여행 조각(메모) API", description = "유저의 여행조각(메모, 이모지)을 정렬 기준에 따라 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getMemoListLatest(
            @RequestParam(value = "sort", defaultValue = "latest") String sort){
        if (!"latest".equals(sort) && !"earliest".equals(sort)) {
            throw new BadRequestHandler(ErrorStatus.INVALID_TRIPPIECE_SORT_OPTION);
        }

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getMemoList(sort);

        if (tripPieceList == null || tripPieceList.isEmpty())
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/picture")
    @Operation(summary = "지난 여행 조각(사진) API", description = "유저의 여행조각(사진, 셀카)을 정렬 기준에 따라 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getPictureListLatest(
            @RequestParam(value = "sort", defaultValue = "latest") String sort) {
        if (!"latest".equals(sort) && !"earliest".equals(sort)) {
            throw new BadRequestHandler(ErrorStatus.INVALID_TRIPPIECE_SORT_OPTION);
        }

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getPictureList(sort);

        if (tripPieceList == null || tripPieceList.isEmpty())
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/video")
    @Operation(summary = "지난 여행 조각(동영상) API", description = "유저의 여행조각(영상, '지금 어디에 있나요?')을 정렬 기준에 따라 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getVideoListLatest(
            @RequestParam(value = "sort", defaultValue = "latest") String sort){
        if (!"latest".equals(sort) && !"earliest".equals(sort)) {
            throw new BadRequestHandler(ErrorStatus.INVALID_TRIPPIECE_SORT_OPTION);
        }

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getVideoList(sort);

        if (tripPieceList == null || tripPieceList.isEmpty())
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/{tripPieceId}")
    @Operation(summary = "단일 여행 조각 조회 API", description = "tripPieceId를 입력받아 여행 조각 출력")
    public ApiResponse<TripPieceResponseDto.getTripPieceDto> getTripPiece(
            @ExistEntity(entityType = TripPiece.class)
            @PathVariable("tripPieceId") Long tripPieceId){
        TripPieceResponseDto.getTripPieceDto response = tripPieceService.getTripPiece(tripPieceId);
        if (response == null)
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE);
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/mytrippieces/delete/{tripPieceId}")
    @Operation(summary = "여행 조각 삭제 API", description = "tripPieceId를 입력 받아 해당 여행 조각을 삭제")
    public ApiResponse<Object> deleteTripPiece(
            @ExistEntity(entityType = TripPiece.class)
            @PathVariable("tripPieceId") Long tripPieceId){
        if (!tripPieceRepository.existsById(tripPieceId))
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE);
        else {
            tripPieceService.delete(tripPieceId);
            return ApiResponse.onSuccess(null);
        }
    }

    @PatchMapping("/mytrippieces/memo/update/{tripPieceId}")
    @Operation(summary = "여행 조각(메모) 수정 API", description = "memo 타입의 tripPiece 수정")
    public ApiResponse<Long> updateMemo(
            @ExistEntity(entityType = TripPiece.class)
            @PathVariable("tripPieceId") Long tripPieceId,
            @RequestBody TripPieceRequestDto.update request){
        return ApiResponse.onSuccess(tripPieceService.memoUpdate(tripPieceId, request));
    }

    @PatchMapping(value = "/mytrippieces/picture/update/{tripPieceId}", consumes = "multipart/form-data")
    @Operation(summary = "여행 조각(사진) 수정 API", description = "picture 타입의 tripPiece 수정")
    public ApiResponse<Long> updatePicture(
            @ExistEntity(entityType = TripPiece.class)
            @PathVariable("tripPieceId") Long tripPieceId,
            @RequestPart TripPieceRequestDto.update request,
            @RequestPart("picture") List<MultipartFile> pictures){
        return ApiResponse.onSuccess(tripPieceService.pictureUpdate(tripPieceId, request, pictures));
    }

    @PatchMapping(value = "/mytrippieces/video/update/{tripPieceId}", consumes = "multipart/form-data")
    @Operation(summary = "여행 조각(영상) 수정 API", description = "video 타입의 tripPiece 수정")
    public ApiResponse<Long> updateVideo(
            @ExistEntity(entityType = TripPiece.class)
            @PathVariable("tripPieceId") Long tripPieceId,
            @RequestPart TripPieceRequestDto.update request,
            @RequestPart("video") MultipartFile video){
        return ApiResponse.onSuccess(tripPieceService.videoUpdate(tripPieceId, request, video));
    }

    @PatchMapping("/mytrippieces/emoji/update/{tripPieceId}")
    @Operation(summary = "여행 조각(이모지) 수정 API", description = "emoji 타입의 tripPiece 수정")
    public ApiResponse<Long> updateEmoji(
            @ExistEntity(entityType = TripPiece.class)
            @PathVariable("tripPieceId") Long tripPieceId,
            @RequestBody TripPieceRequestDto.update request,
            @RequestPart("emoji") List<String> emojis){
        return ApiResponse.onSuccess(tripPieceService.emojiUpdate(tripPieceId, request, emojis));
    }
}
