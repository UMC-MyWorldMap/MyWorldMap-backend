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

@RestController
@RequestMapping("/user/kakao")
public class KakaoController {

    private final UserService userService;
    private final JWTUtil jwtUtil;


    @Autowired
    public KakaoController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    @Operation(summary = "카카오 회원가입 API", description = "카카오 로그인 후 진행하는 회원가입")
    public ApiResponse<UserResponseDto.SignUpKakaoResultDto> signUp(@RequestPart("info") @Valid UserRequestDto.SignUpKakaoDto request, @RequestPart("profileImg") MultipartFile profileImg) {
        try {
            User user = userService.signUpKakao(request, profileImg);
            return ApiResponse.onSuccess(UserConverter.toSignUpKakaoResultDto(user));
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure("400", e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "카카오 로그인 API",
    description = "카카오 계정의 존재 여부 확인")
    public ApiResponse<UserResponseDto.LoginKakaoResultDto> login(@RequestBody @Valid UserRequestDto.LoginKakaoDto request) {
        User user = userService.loginKakao(request);

        if (user != null) {
            // 로그인 성공 시 토큰 생성
            String accessToken = jwtUtil.createAccessToken(request.getEmail());
            String refreshToken = user.getRefreshToken();

            return ApiResponse.onSuccess(UserConverter.toLoginKakaoResultDto(user, accessToken, refreshToken));
        } else {
            // 유저 정보가 없을 경우 회원가입 페이지로 이동하도록 응답
            return ApiResponse.onFailure("404", "카카오 회원정보가 없습니다.", null);
        }
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
