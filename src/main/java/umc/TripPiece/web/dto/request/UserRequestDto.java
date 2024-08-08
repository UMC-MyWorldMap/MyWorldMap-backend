package umc.TripPiece.web.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.TripPiece.domain.enums.Gender;

public class UserRequestDto {

    /* 회원가입 */
    @Getter
    @NoArgsConstructor
    public static class SignUpDto {

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(min = 2, max = 10, message = "이름은 2자에서 10자 사이여야 합니다.")
        private String name;

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 주소여야 합니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자에서 15자 사이여야 합니다."
        )
        private String password;

        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자에서 10자 사이여야 합니다.")
        private String nickname;

        @NotNull(message = "성별은 필수 입력 항목입니다.")
        private Gender gender;

        @NotBlank(message = "생일은 필수 입력 항목입니다.")
        @Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$", message = "생일은 YYYY/MM/DD 형식이어야 합니다.")
        private String birth;

        private String profileImg;

        @NotBlank(message = "국적은 필수 입력 항목입니다.")
        @Pattern(regexp = "^South Korea$", message = "국적은 현재 대한민국만 이용 가능합니다.")
        private String country;
    }


    /* 로그인 */
    @Getter
    public static class LoginDto {
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 주소여야 합니다.")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        String password;
    }

    /* 토큰 재발급 */
    @Getter
    public static class ReissueDto {
        @NotBlank(message = "refreshToken은 필수 입력 항목입니다.")
        String refreshToken;
    }

}