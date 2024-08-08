package umc.TripPiece.payload.exception.handler;

import umc.TripPiece.payload.code.BaseErrorCode;
import umc.TripPiece.payload.exception.GeneralException;

public class EmojiHandler extends GeneralException {
    public EmojiHandler(BaseErrorCode errorCode) {super(errorCode);}
}
