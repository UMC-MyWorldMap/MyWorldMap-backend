package umc.TripPiece.validation;

public class RegexConstants {

    // 정규식
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.(com|net|org|co\\.kr|ac\\.kr|gov|edu)$";
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$";
    public static final String BIRTH_REGEX = "^(19|20)\\d{2}/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$";
    public static final String COUNTRY_REGEX = "^South Korea$";

    private RegexConstants() {
    }
}
