package umc.TripPiece.payload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.TripPiece.payload.code.BaseErrorCode;
import umc.TripPiece.payload.code.ErrorReasonDTO;


@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // s3 관련 오류
    PICTURE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "PICTURE400", "이미지의 확장자가 잘못되었습니다."),
    VIDEO_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "VIDEO400", "동영상의 확장자가 잘못되었습니다."),

    // 이모지 오류
    EMOJI_NUMBER_ERROR(HttpStatus.BAD_REQUEST, "EMOJI400", "이모지의 갯수는 4개이여야 합니다."),
    EMOJI_INPUT_ERROR(HttpStatus.BAD_REQUEST, "EMOJI401", "이모지가 아닌 입력이 있습니다."),

    // 글자 수 오류
    TEXT_LENGTH_30_ERROR(HttpStatus.BAD_REQUEST, "TEXT400", "글자 수가 30자를 초과하였습니다."),
    TEXT_LENGTH_100_ERROR(HttpStatus.BAD_REQUEST, "TEXT401", "글자 수가 100자를 초과하였습니다.");

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
