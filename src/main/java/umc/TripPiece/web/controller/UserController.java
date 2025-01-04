package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.apiPayload.ApiResponse;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.GeneralException;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.apiPayload.exception.handler.UserHandler;
import umc.TripPiece.converter.UserConverter;
import umc.TripPiece.domain.User;
import umc.TripPiece.domain.enums.UserMethod;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.service.UserService;
import umc.TripPiece.web.dto.request.UserRequestDto;
import umc.TripPiece.web.dto.response.UserResponseDto;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    @Operation(summary = "일반 회원가입 API", description = "일반 회원가입")
    public ApiResponse<UserResponseDto.SignUpResultDto> signUp(
            @RequestPart("info") @Valid UserRequestDto.SignUpDto request,
            @RequestPart("profileImg") MultipartFile profileImg) {
        try {
            User user = userService.signUp(request, profileImg);
            return ApiResponse.onSuccess(UserConverter.toSignUpResultDto(user));
        } catch (UserHandler e) {
            return ApiResponse.onFailure(e.getCode().getReason().getCode(), e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "이메일 로그인 API", description = "이메일 로그인 (일반)")
    public ApiResponse<UserResponseDto.LoginResultDto> login(@RequestBody @Valid UserRequestDto.LoginDto request) {
        try {
            User user = userService.login(request);

            // 로그인 성공 시 토큰 생성
            String accessToken = jwtUtil.createAccessToken(request.getEmail());
            String refreshToken = user.getRefreshToken();
            return ApiResponse.onSuccess(UserConverter.toLoginResultDto(user, accessToken, refreshToken));
        } catch (UserHandler e) {
            return ApiResponse.onFailure(e.getCode().getReason().getCode(), e.getMessage(), null);
        }
    }

    @PostMapping(value = "/signup/{platform}", consumes = "multipart/form-data")
    @Operation(summary = "소셜 회원가입 API", description = "소셜 로그인 후 진행하는 회원가입")
    public ApiResponse<UserResponseDto.SignUpSocialResultDto> signUp(@Parameter(
            name = "platform",
            description = "소셜 로그인 플랫폼 (KAKAO 또는 APPLE)",
            required = true,
            in = ParameterIn.PATH
    )@PathVariable("platform") String platform, @RequestPart("info") @Valid UserRequestDto.SignUpSocialDto request, @RequestPart("profileImg") MultipartFile profileImg) {
        UserMethod method;
        try {
            method = UserMethod.valueOf(platform.toUpperCase());
            // 일반 회원가입 요청 방지
            if (method == UserMethod.GENERAL) {
                throw new BadRequestHandler(ErrorStatus.GENERAL_BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestHandler(ErrorStatus.PLATFORM_BAD_REQUEST);
        }

        try {
            User user = userService.signUpSocial(method, request, profileImg);
            return ApiResponse.onSuccess(UserConverter.toSignUpKakaoResultDto(user));
        } catch (UserHandler e) {
            return ApiResponse.onFailure(e.getCode().getReason().getCode(), e.getMessage(), null);
        }
    }

    @PostMapping("/login/{platform}")
    @Operation(summary = "소셜 로그인 API",
            description = "카카오/애플 계정의 존재 여부 확인")
    public ApiResponse<UserResponseDto.LoginSocialResultDto> login(@Parameter(
            name = "platform",
            description = "소셜 로그인 플랫폼 (KAKAO 또는 APPLE)",
            required = true,
            in = ParameterIn.PATH
    )@PathVariable("platform") String platform, @RequestBody @Valid UserRequestDto.LoginSocialDto request) {

        UserMethod method;
        try {
            method = UserMethod.valueOf(platform.toUpperCase());
            // 일반 로그인 요청 방지
            if (method == UserMethod.GENERAL) {
                throw new BadRequestHandler(ErrorStatus.GENERAL_BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestHandler(ErrorStatus.PLATFORM_BAD_REQUEST);
        }

        User user;
        user = userService.loginSocial(request, method);
        if (user != null) {
            // 로그인 성공 시 토큰 생성
            String accessToken = jwtUtil.createAccessToken(request.getEmail());
            String refreshToken = user.getRefreshToken();

            return ApiResponse.onSuccess(UserConverter.toLoginSocialResultDto(user, accessToken, refreshToken));
        } else {
            // 유저 정보가 없을 경우 회원가입 페이지로 이동하도록 응답
            String errorMessage = method.name() + " 회원정보가 없습니다.";
            throw new NotFoundHandler(ErrorStatus.SOCIAL_NOT_FOUND, errorMessage);
        }
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급 API", description = "refresh token을 통한 access token, refresh token 재발급")
    public ApiResponse<UserResponseDto.ReissueResultDto> refresh(
            @RequestBody @Valid UserRequestDto.ReissueDto request) {
        User user = userService.reissue(request);
        String newAccessToken = jwtUtil.createAccessToken(user.getEmail());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getEmail());
        user.setRefreshToken(newRefreshToken);
        userService.save(user);
        return ApiResponse.onSuccess(UserConverter.toReissueResultDto(newAccessToken, newRefreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃")
    public ApiResponse<String> logout(@RequestHeader("Authorization") String token) {
        if (!token.startsWith("Bearer ")) {
            return ApiResponse.onFailure("400", "유효하지 않은 토큰 형식입니다.", null);
        }

        String tokenWithoutBearer = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(tokenWithoutBearer);
            if (userId == null) {
                return ApiResponse.onFailure("400", "존재하지 않거나 만료된 토큰입니다.", null);
            }
            userService.logout(userId);
            return ApiResponse.onSuccess("로그아웃에 성공했습니다.");
        } catch (Exception e) {
            return ApiResponse.onFailure("400", e.getMessage(), null);
        }
    }

    @DeleteMapping("/withdrawal")
    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴")
    public ApiResponse<String> withdrawal(@RequestHeader("Authorization") String token) {
        if (!token.startsWith("Bearer ")) {
            return ApiResponse.onFailure("400", "유효하지 않은 토큰 형식입니다.", null);
        }

        String tokenWithoutBearer = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(tokenWithoutBearer);
            if (userId == null) {
                return ApiResponse.onFailure("400", "존재하지 않거나 만료된 토큰입니다.", null);
            }
            userService.withdrawal(userId);
            return ApiResponse.onSuccess("회원탈퇴에 성공했습니다.");
        } catch (Exception e) {
            return ApiResponse.onFailure("400", e.getMessage(), null);
        }
    }

    @PatchMapping(value = "/update", consumes = "multipart/form-data")
    @Operation(summary = "프로필 수정하기 API", description = "프로필 수정하기")
    public ApiResponse<UserResponseDto.UpdateResultDto> update(
            @RequestPart("info") @Valid UserRequestDto.UpdateDto request,
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {
        try {
            String tokenWithoutBearer = token.substring(7);
            User user = userService.update(request, tokenWithoutBearer, profileImg);
            return ApiResponse.onSuccess(UserConverter.toUpdateResultDto(user));
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.PROFILE_UPDATE_FAILED);
        }
    }

    @GetMapping("/myprofile")
    @Operation(summary = "프로필 조회 API", description = "마이페이지 프로필 조회")
    public ApiResponse<UserResponseDto.ProfileDto> getProfile(@RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.substring(7);
        return ApiResponse.onSuccess(userService.getProfile(tokenWithoutBearer));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String combinedMessage = String.join(", ", errors.values());

        return new ResponseEntity<>(ApiResponse.onFailure("400", combinedMessage, errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ApiResponse.onFailure("400", ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
