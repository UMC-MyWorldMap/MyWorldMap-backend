package umc.TripPiece.payload.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorReasonDTO {
    private final String code;
    private final String message;
    private final boolean isSuccess;
    private final HttpStatus httpStatus;

    public ErrorReasonDTO(String code, String message, boolean isSuccess, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.isSuccess = isSuccess;
        this.httpStatus = httpStatus;
    }


}
