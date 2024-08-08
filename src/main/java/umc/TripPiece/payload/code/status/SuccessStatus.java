package umc.TripPiece.payload.code.status;

import lombok.Getter;

@Getter
public enum SuccessStatus {
    _OK("200", "Operation successful");

    private final String code;
    private final String message;

    SuccessStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }
}