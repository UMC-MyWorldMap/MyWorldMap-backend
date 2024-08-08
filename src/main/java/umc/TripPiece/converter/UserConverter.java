package umc.TripPiece.converter;

import umc.TripPiece.domain.User;
import umc.TripPiece.domain.enums.Gender;
import umc.TripPiece.domain.enums.UserMethod;
import umc.TripPiece.web.dto.request.UserRequestDto;
import umc.TripPiece.web.dto.response.UserResponseDto;

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

    public static UserResponseDto.LoginResultDto toLoginResultDto(User user, String accessToken, String refreshToken){
        return UserResponseDto.LoginResultDto.builder()
                .email(user.getEmail())
                .id(user.getId())
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


    public static User toUser(UserRequestDto.SignUpDto request, String hashedPassword) {
        Gender gender = request.getGender();

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .nickname(request.getNickname())
                .gender(gender)
                .birth(request.getBirth())
                .profileImg(request.getProfileImg())
                .country(request.getCountry())
                .gpsConsent(true) // 고정값 설정
                .method(UserMethod.GENERAL) // 고정값 설정
                .isPublic(true) // 고정값 설정
                .build();
    }
}
