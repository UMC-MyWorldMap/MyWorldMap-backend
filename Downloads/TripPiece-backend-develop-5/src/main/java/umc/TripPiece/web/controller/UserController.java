package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.converter.UserConverter;
import umc.TripPiece.domain.User;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.payload.ApiResponse;
import umc.TripPiece.service.UserService;
import umc.TripPiece.web.dto.request.UserRequestDto;
import umc.TripPiece.web.dto.response.UserResponseDto;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;


    @Autowired
    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    @Operation(summary = "회원가입 API",
    description = "회원가입")
    public ApiResponse<UserResponseDto.SignUpResultDto> signUp(@RequestPart("info") @Valid UserRequestDto.SignUpDto request, @RequestPart("profileImg") MultipartFile profileImg) {
        try {
            User user = userService.signUp(request, profileImg);
            return ApiResponse.onSuccess(UserConverter.toSignUpResultDto(user));
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure("400", e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "이메일 로그인 API",
    description = "이메일 로그인 (일반)")
    public ApiResponse<UserResponseDto.LoginResultDto> login(@RequestBody @Valid UserRequestDto.LoginDto request) {
        User user = userService.login(request);

        if (user != null) {
            // 로그인 성공 시 토큰 생성
            String accessToken = jwtUtil.createAccessToken(request.getEmail());
            String refreshToken = user.getRefreshToken();

            return ApiResponse.onSuccess(UserConverter.toLoginResultDto(user, accessToken, refreshToken));
        } else {
            return ApiResponse.onFailure("400", "로그인에 실패했습니다.", null);
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
    public ApiResponse<String> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ApiResponse.onFailure("400", "토큰이 유효하지 않습니다.", null);
        }

        String token = header.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            userService.logout(userId);
            return ApiResponse.onSuccess("로그아웃에 성공했습니다.");
        } catch (Exception e) {
            return ApiResponse.onFailure("400", e.getMessage(), null);
        }
    }

    @PostMapping(value = "/update", consumes = "multipart/form-data")
    @Operation(summary = "프로필 수정하기 API",
            description = "프로필 수정하기")
    public ApiResponse<UserResponseDto.UpdateResultDto> update(@RequestPart("info") @Valid UserRequestDto.UpdateDto request, @RequestHeader("Authorization") String token, @RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {
        String tokenWithoutBearer = token.substring(7);
        User user = userService.update(request, tokenWithoutBearer, profileImg);

        if (user != null) {
            return ApiResponse.onSuccess(UserConverter.toUpdateResultDto(user));
        } else {
            return ApiResponse.onFailure("400", "프로필 수정에 실패했습니다.", null);
        }
    }

    @GetMapping("/myprofile")
    @Operation(summary = "프로필 조회 API",
            description = "마이페이지 프로필 조회")
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

        String combinedMessage = String.join(" + ", errors.values());

        return new ResponseEntity<>(ApiResponse.onFailure("400", combinedMessage, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ApiResponse.onFailure("400", ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
