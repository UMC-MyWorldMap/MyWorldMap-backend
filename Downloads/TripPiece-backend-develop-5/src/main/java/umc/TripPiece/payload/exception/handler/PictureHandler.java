package umc.TripPiece.payload.exception.handler;

import umc.TripPiece.payload.code.BaseErrorCode;
import umc.TripPiece.payload.exception.GeneralException;

public class PictureHandler extends GeneralException {
    public PictureHandler(BaseErrorCode errorCode) {super(errorCode);}
}
