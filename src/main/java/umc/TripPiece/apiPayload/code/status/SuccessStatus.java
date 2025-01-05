package umc.TripPiece.apiPayload.code.status;

import lombok.Getter;

@Getter
public enum SuccessStatus {
    _OK("COMMON200", "요청에 성공했습니다.");

    private final String code;
    private final String message;

    SuccessStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }
}