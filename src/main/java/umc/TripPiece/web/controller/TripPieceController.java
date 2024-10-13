package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.TripPiece.payload.ApiResponse;
import umc.TripPiece.repository.TripPieceRepository;
import umc.TripPiece.service.TripPieceService;
import umc.TripPiece.web.dto.request.TripPieceRequestDto;
import umc.TripPiece.web.dto.response.TripPieceResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TripPieceController {

    private final TripPieceService tripPieceService;
    private final TripPieceRepository tripPieceRepository;

    @GetMapping("/mytrippieces/all/latest")
    @Operation(summary = "지난 여행 조각(전체, 최신순) API", description = "유저의 여행조각(전체)을 최신순으로 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getTripPieceListLatest(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getTripPieceListLatest(tokenWithoutBearer);
        if (tripPieceList == null || tripPieceList.isEmpty())
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/all/earliest")
    @Operation(summary = "지난 여행 조각(전체, 오래된순) API", description = "유저의 여행조각(전체)을 오래된순으로 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getTripPieceListEarliest(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getTripPieceListEarliest(tokenWithoutBearer);
        if (tripPieceList == null || tripPieceList.isEmpty())
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/memo/latest")
    @Operation(summary = "지난 여행 조각(메모, 최신순) API", description = "유저의 여행조각(메모, 이모지)을 최신순으로 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getMemoListLatest(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getMemoListLatest(tokenWithoutBearer);
        if (tripPieceList == null || tripPieceList.isEmpty())
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/memo/earliest")
    @Operation(summary = "지난 여행 조각(메모, 오래된순) API", description = "유저의 여행조각(메모, 이모지)을 오래된순으로 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getMemoListEarliest(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getMemoListEarliest(tokenWithoutBearer);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/picture/latest")
    @Operation(summary = "지난 여행 조각(사진, 최신순) API", description = "유저의 여행조각(사진, 셀카)을 최신순으로 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getPictureListLatest(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getPictureListLatest(tokenWithoutBearer);
        if (tripPieceList == null || tripPieceList.isEmpty())
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/picture/earliest")
    @Operation(summary = "지난 여행 조각(사진, 오래된순) API", description = "유저의 여행조각(사진, 셀카)을 오래된순으로 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getPictureListEarliest(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getPictureListEarliest(tokenWithoutBearer);
        if (tripPieceList == null || tripPieceList.isEmpty())
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/video/latest")
    @Operation(summary = "지난 여행 조각(동영상, 최신순) API", description = "유저의 여행조각(영상, '지금 어디에 있나요?')을 최신순으로 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getVideoListLatest(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getVideoListLatest(tokenWithoutBearer);
        if (tripPieceList == null || tripPieceList.isEmpty())
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/video/earliest")
    @Operation(summary = "지난 여행 조각(동영상, 오래된순) API", description = "유저의 여행조각(영상, '지금 어디에 있나요?')을 오래된순으로 반환")
    public ApiResponse<List<TripPieceResponseDto.TripPieceListDto>> getVideoListEarliest(@RequestHeader("Authorization") String token){
        String tokenWithoutBearer = token.substring(7);
        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = tripPieceService.getVideoListEarliest(tokenWithoutBearer);
        if (tripPieceList == null || tripPieceList.isEmpty())
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        return ApiResponse.onSuccess(tripPieceList);
    }

    @GetMapping("/mytrippieces/{tripPieceId}")
    @Operation(summary = "단일 여행 조각 조회 API", description = "tripPieceId를 입력받아 여행 조각 출력")
    public ApiResponse<TripPieceResponseDto.getTripPieceDto> getTripPiece(@PathVariable("tripPieceId") Long tripPieceId){
        TripPieceResponseDto.getTripPieceDto response = tripPieceService.getTripPiece(tripPieceId);
        if (response == null)
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/mytrippieces/{tripPieceId}/delete")
    @Operation(summary = "여행 조각 삭제 API", description = "tripPieceId를 입력 받아 해당 여행 조각을 삭제")
    public ApiResponse<Object> deleteTripPiece(@PathVariable("tripPieceId") Long tripPieceId){
        if (!tripPieceRepository.existsById(tripPieceId))
            return ApiResponse.onFailure("400", "여행 조각이 존재하지 않습니다.", null);
        else {
            tripPieceService.delete(tripPieceId);
            return ApiResponse.onSuccess(null);
        }
    }

    @PatchMapping("/mytrippieces/memo/{tripPieceId}/update")
    @Operation(summary = "여행 조각(메모) 수정 API", description = "memo 타입의 tripPiece 수정")
    public ApiResponse<Long> updateMemo(@PathVariable("tripPieceId") Long tripPieceId, @RequestBody TripPieceRequestDto.MemoUpdateDto request){
        return ApiResponse.onSuccess(tripPieceService.memoUpdate(tripPieceId, request));
    }
}
