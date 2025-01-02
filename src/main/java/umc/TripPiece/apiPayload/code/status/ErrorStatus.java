package umc.TripPiece.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.TripPiece.apiPayload.code.BaseErrorCode;
import umc.TripPiece.apiPayload.code.ErrorReasonDTO;


@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // Not Found
    NOT_FOUND_MAP(HttpStatus.NOT_FOUND, "MAP404", "해당 맵을 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER404", "해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_CITY(HttpStatus.NOT_FOUND, "CITY404", "해당 도시를 찾을 수 없습니다."),
    NOT_FOUND_TRAVEL(HttpStatus.NOT_FOUND, "TRAVEL404", "해당 여행기를 찾을 수 없습니다."),

    // s3 관련 오류
    PICTURE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "PICTURE400", "이미지의 확장자가 잘못되었습니다."),
    VIDEO_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "VIDEO400", "동영상의 확장자가 잘못되었습니다."),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "UPLOAD413", "파일 크기가 허용 범위를 초과했습니다."),

    // 이모지 오류
    EMOJI_NUMBER_ERROR(HttpStatus.BAD_REQUEST, "EMOJI400", "이모지의 갯수는 4개이여야 합니다."),
    EMOJI_INPUT_ERROR(HttpStatus.BAD_REQUEST, "EMOJI401", "이모지가 아닌 입력이 있습니다."),

    // 글자 수 오류
    TEXT_LENGTH_30_ERROR(HttpStatus.BAD_REQUEST, "TEXT400", "글자 수가 30자를 초과하였습니다."),
    TEXT_LENGTH_100_ERROR(HttpStatus.BAD_REQUEST, "TEXT401", "글자 수가 100자를 초과하였습니다."),

    // 사용자 관련 오류
    INVALID_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "PROFILE400", "잘못된 프로필 이미지 형식입니다."),
    PROFILE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PROFILE500", "프로필 업데이트에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN401", "유효하지 않은 토큰입니다."),

    // 여행 조각 관련 오류
    NOT_FOUND_TRIPPIECE(HttpStatus.NOT_FOUND, "TRIPPIECE404", "여행 조각이 존재하지 않습니다"),
    INVALID_TRIPPIECE_SORT_OPTION(HttpStatus.BAD_REQUEST, "TRIPPIECE401", "유효하지 않은 정렬 조건입니다."),

    // 여행기 관련 오류
    INVALID_TRAVEL_PARARM(HttpStatus.BAD_REQUEST, "TRAVEL400", "유효하지 않은 파라미터입니다."),
    TRAVEL_INPROGRESS(HttpStatus.BAD_REQUEST, "TRAVEL400", "현재 진행 중인 여행기가 있습니다."),
    MISSING_TRAVEL_THUMBNAIL(HttpStatus.BAD_REQUEST,"TRAVEL400","썸네일이 필요합니다."),
    INVALID_TRAVEL_DATE(HttpStatus.BAD_REQUEST, "TRAVEL400", "여행 기간이 유효하지 않습니다."),
    INVALID_TRAVEL_TITLE(HttpStatus.BAD_REQUEST, "TRAVEL400", "여행기 제목이 유효하지 않습니다."),

    // 요청 정보를 가지고 올 수 없을 때
    REQUEST_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "REQUEST404", "요청 정보를 가져올 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
