package umc.TripPiece.payload.exception.handler;

import umc.TripPiece.payload.code.BaseErrorCode;
import umc.TripPiece.payload.exception.GeneralException;

public class VideoHandler extends GeneralException {
    public VideoHandler(BaseErrorCode errorCode) {super(errorCode);}
}
