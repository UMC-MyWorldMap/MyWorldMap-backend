package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API",
    description = "회원가입")
    public ApiResponse<UserResponseDto.SignUpResultDto> signUp(@RequestBody @Valid UserRequestDto.SignUpDto request) {
        try {
            User user = userService.signUp(request);
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
            String refreshToken = jwtUtil.createRefreshToken(request.getEmail());

            return ApiResponse.onSuccess(UserConverter.toLoginResultDto(user, accessToken, refreshToken));
        } else {
            return ApiResponse.onFailure("400", "로그인에 실패했습니다.", null);
        }
    }

`   @PostMapping("/update")
    @Operation(summary = "프로필 수정하기 API",
            description = "프로필 수정하기")
    public ApiResponse<UserResponseDto.UpdateResultDto> update(@RequestBody @Valid UserRequestDto.UpdateDto request) {
        User user = userService.update(request);

        if (user != null) {
            return ApiResponse.onSuccess(UserConverter.toUpdateResultDto(user));
        } else {
            return ApiResponse.onFailure("400", "프로필 수정에 실패했습니다.", null);
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
