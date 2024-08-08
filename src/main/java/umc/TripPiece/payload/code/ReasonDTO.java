package umc.TripPiece.payload.code;

public class ReasonDTO {
    private final String code;
    private final String message;

    public ReasonDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}