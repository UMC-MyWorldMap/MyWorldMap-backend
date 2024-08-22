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
        String nickname;
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

    /* 카카오 회원가입 */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpKakaoResultDto {
        private Long id;
        private Long providerId;
        private String name;
        private String email;
        private String nickname;
        private Gender gender;
        private String birth;
        private String profileImg;
        private String country;
        private LocalDateTime createdAt;
    }

    /* 카카오 로그인 */
    @Builder
    @Getter
    @AllArgsConstructor
    public static class LoginKakaoResultDto {
        private Long id;
        private Long providerId;
        private String email;
        private String nickname;
        private LocalDateTime createdAt;
        private String accessToken;
        private String refreshToken;
    }

    /* 프로필 수정 */
    @Builder
    @Getter
    @AllArgsConstructor
    public static class UpdateResultDto {
        String nickname;
        Gender gender;
        String birth;
        String country;
        String profileImg;
    }

    /* 프로필 조회 */
    @Builder
    @Getter
    @AllArgsConstructor
    public static class ProfileDto {
        String nickname;
        String profileImg;
        Integer travelNum;
        Boolean isPublic;
        Gender gender;
        String country;
        String birth;
    }

}
