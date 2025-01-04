package umc.TripPiece.apiPayload.exception;

import lombok.Getter;
import umc.TripPiece.apiPayload.code.BaseErrorCode;
import umc.TripPiece.apiPayload.code.ErrorReasonDTO;

@Getter
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public GeneralException(BaseErrorCode errorCode) {
        super(errorCode.getReason().getMessage()); // 메시지 초기화
        this.code = errorCode;
    }

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}