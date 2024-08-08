package umc.TripPiece.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.TripPiece.domain.enums.Gender;

import java.time.LocalDateTime;

public class UserResponseDto {
    /* 회원가입 */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpResultDto {
        Long id;
        String name;
        String email;
        String nickname;
        Gender gender;
        String birth;
        String profileImg;
        String country;
        LocalDateTime createdAt;

    }

    /* 로그인 */
    @Builder
    @Getter
    @AllArgsConstructor
    public static class LoginResultDto {
        Long id;
        String email;
        String name;
        LocalDateTime createdAt;
        String accessToken;
        String refreshToken;
    }

    /* 토큰 재발급 */
    @Builder
    @Getter
    @AllArgsConstructor
    public static class ReissueResultDto {
        String accessToken;
        String refreshToken;
    }
}
