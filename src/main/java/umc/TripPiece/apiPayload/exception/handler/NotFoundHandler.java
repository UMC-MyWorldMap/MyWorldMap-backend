package umc.TripPiece.apiPayload.exception.handler;

import umc.TripPiece.apiPayload.code.BaseErrorCode;
import umc.TripPiece.apiPayload.exception.GeneralException;

public class NotFoundHandler extends GeneralException {
    private final String customMessage;

    public NotFoundHandler(BaseErrorCode errorCode) {
        super(errorCode);
        this.customMessage = null;
    }

    public NotFoundHandler(BaseErrorCode errorCode, String customMessage) {
        super(errorCode);
        this.customMessage = customMessage;
    }

    @Override
    public String getMessage() {
        return customMessage != null ? customMessage : super.getMessage();
    }
}
