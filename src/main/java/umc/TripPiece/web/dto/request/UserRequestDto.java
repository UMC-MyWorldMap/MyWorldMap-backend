package umc.TripPiece.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.TripPiece.domain.enums.Gender;
import umc.TripPiece.validation.RegexConstants;
import umc.TripPiece.validation.annotation.ValidBirth;

public class UserRequestDto {

    /* 회원가입 */
    @Getter
    @NoArgsConstructor
    public static class SignUpDto {

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(min = 2, max = 10, message = "이름은 2자에서 10자 사이여야 합니다.")
        private String name;

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email
        @Pattern(
                regexp = RegexConstants.EMAIL_REGEX,
                message = "유효한 이메일 주소여야 합니다."
        )
        @Schema(example = "trippiece@example.com", description = "유효한 이메일 주소")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Pattern(
                regexp = RegexConstants.PASSWORD_REGEX,
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자에서 15자 사이여야 합니다."
        )
        private String password;

        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자에서 10자 사이여야 합니다.")
        private String nickname;

        @NotNull(message = "성별은 필수 입력 항목입니다.")
        private Gender gender;


        @ValidBirth
        @NotBlank(message = "생일은 필수 입력 항목입니다.")
        @Pattern(
                regexp = RegexConstants.BIRTH_REGEX,
                message = "생일은 유효한 날짜여야 하며, YYYY/MM/DD 형식이어야 합니다."
        )
        private String birth;

        @NotBlank(message = "국적은 필수 입력 항목입니다.")
        @Pattern(
                regexp = RegexConstants.COUNTRY_REGEX,
                message = "국적은 현재 대한민국만 이용 가능합니다."
        )
        private String country;
    }


    /* 로그인 */
    @Getter
    public static class LoginDto {
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Pattern(
                regexp = RegexConstants.EMAIL_REGEX,
                message = "유효한 이메일 주소여야 합니다."
        )
        @Schema(example = "trippiece@example.com", description = "유효한 이메일 주소")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        String password;
    }

    /* 소셜 회원가입 */
    @Getter
    @NoArgsConstructor
    public static class SignUpSocialDto {
        @NotNull(message = "유저 ID는 필수 입력 항목입니다.")
        private Long providerId;

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(min = 2, max = 10, message = "이름은 2자에서 10자 사이여야 합니다.")
        private String name;

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Pattern(
                regexp = RegexConstants.EMAIL_REGEX,
                message = "유효한 이메일 주소여야 합니다."
        )
        @Schema(example = "trippiece@example.com", description = "유효한 이메일 주소")
        private String email;

        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자에서 10자 사이여야 합니다.")
        private String nickname;

        @NotNull(message = "성별은 필수 입력 항목입니다.")
        private Gender gender;

        @ValidBirth
        @NotBlank(message = "생일은 필수 입력 항목입니다.")
        @Pattern(
                regexp = RegexConstants.BIRTH_REGEX,
                message = "생일은 유효한 날짜여야 하며, YYYY/MM/DD 형식이어야 합니다."
        )
        private String birth;

        @NotBlank(message = "국적은 필수 입력 항목입니다.")
        @Pattern(
                regexp = RegexConstants.COUNTRY_REGEX,
                message = "국적은 현재 대한민국만 이용 가능합니다."
        )
        private String country;
    }

    /* 소셜 로그인 */
    @Getter
    public static class LoginSocialDto {
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Pattern(
                regexp = RegexConstants.EMAIL_REGEX,
                message = "유효한 이메일 주소여야 합니다."
        )
        @Schema(example = "trippiece@example.com", description = "유효한 이메일 주소")
        private String email;

        @NotNull(message = "유저 ID는 필수 입력 항목입니다.")
        private Long providerId;
    }

    /* 토큰 재발급 */
    @Getter
    public static class ReissueDto {
        @NotBlank(message = "refreshToken은 필수 입력 항목입니다.")
        String refreshToken;
    }

    /* 프로필 수정 */
    @Getter
    public static class UpdateDto {

        @Size(min = 2, max = 10, message = "닉네임은 2자에서 10자 사이여야 합니다.")
        private String nickname;

        private Gender gender;

        @ValidBirth
        @Pattern(
                regexp = RegexConstants.BIRTH_REGEX,
                message = "생일은 유효한 날짜여야 하며, YYYY/MM/DD 형식이어야 합니다."
        )
        private String birth;

        @Pattern(
                regexp = RegexConstants.COUNTRY_REGEX,
                message = "국적은 현재 대한민국만 이용 가능합니다."
        )
        private String country;
    }
}