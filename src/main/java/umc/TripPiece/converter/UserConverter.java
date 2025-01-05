package umc.TripPiece.converter;

import umc.TripPiece.domain.User;
import umc.TripPiece.domain.enums.Gender;
import umc.TripPiece.domain.enums.UserMethod;
import umc.TripPiece.web.dto.request.UserRequestDto;
import umc.TripPiece.web.dto.response.UserResponseDto;
import umc.TripPiece.web.dto.response.UserResponseDto.SignUpSocialResultDto;

import java.time.LocalDateTime;

public class UserConverter {

    public static UserResponseDto.SignUpResultDto toSignUpResultDto(User user){
        return UserResponseDto.SignUpResultDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birth(user.getBirth())
                .profileImg(user.getProfileImg())
                .country(user.getCountry())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now())
                .build();
    }

    public static SignUpSocialResultDto toSignUpKakaoResultDto(User user){
        return UserResponseDto.SignUpSocialResultDto.builder()
                .id(user.getId())
                .providerId(user.getProviderId())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birth(user.getBirth())
                .profileImg(user.getProfileImg())
                .country(user.getCountry())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now())
                .build();
    }

    public static UserResponseDto.LoginResultDto toLoginResultDto(User user, String accessToken, String refreshToken){
        return UserResponseDto.LoginResultDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static UserResponseDto.LoginSocialResultDto toLoginSocialResultDto(User user, String accessToken, String refreshToken){
        return UserResponseDto.LoginSocialResultDto.builder()
                .id(user.getId())
                .providerId(user.getProviderId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static UserResponseDto.ReissueResultDto toReissueResultDto(String accessToken, String refreshToken){
        return UserResponseDto.ReissueResultDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static UserResponseDto.ProfileDto toProfileDto(User user, Integer travelNum) {
        return UserResponseDto.ProfileDto.builder()
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .travelNum(travelNum)
                .isPublic(user.getIsPublic())
                .gender(user.getGender())
                .country(user.getCountry())
                .birth(user.getBirth())
                .build();
    }


    /* 일반 회원가입용 */
    public static User toUser(UserRequestDto.SignUpDto request, String hashedPassword) {
        Gender gender = request.getGender();

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .nickname(request.getNickname())
                .gender(gender)
                .birth(request.getBirth())
                .country(request.getCountry())
                .gpsConsent(true) // 고정값 설정
                .method(UserMethod.GENERAL) // 고정값 설정
                .isPublic(true) // 고정값 설정
                .build();
    }

    /* 소셜 회원가입용 */
    public static User toSocialUser(UserRequestDto.SignUpSocialDto request, UserMethod method) {
        Gender gender = request.getGender();

        return User.builder()
                .name("")
                .email(request.getEmail())
                .password("")
                .nickname(request.getNickname())
                .gender(gender)
                .birth(request.getBirth())
                .country(request.getCountry())
                .gpsConsent(true) // 고정값 설정
                .method(method) // KAKAO 또는 APPLE
                .providerId(request.getProviderId()) // 카카오 또는 애플 providerId
                .isPublic(true) // 고정값 설정
                .build();
    }

    public static UserResponseDto.UpdateResultDto toUpdateResultDto(User user){
        return UserResponseDto.UpdateResultDto.builder()
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birth(user.getBirth())
                .country(user.getCountry())
                .profileImg(user.getProfileImg())
                .build();
    }
}
