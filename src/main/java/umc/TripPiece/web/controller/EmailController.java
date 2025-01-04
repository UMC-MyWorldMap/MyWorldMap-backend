package umc.TripPiece.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.UserHandler;
import umc.TripPiece.domain.VerificationCode;
import umc.TripPiece.apiPayload.ApiResponse;
import umc.TripPiece.repository.VerificationCodeRepository;
import umc.TripPiece.service.EmailService;
import umc.TripPiece.web.dto.request.EmailRequestDto;

@Tag(name = "Email", description = "이메일 인증 관련 API")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    @PostMapping("/send")
    @Operation(summary = "이메일 인증번호 전송 API",
            description = "이메일로 6자리 인증번호 발송")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestBody @Valid EmailRequestDto.SendCodeDto request) {
        String email = request.getEmail();
        String code = emailService.generateVerificationCode();

        VerificationCode verificationCode = new VerificationCode(email, code, 3); // 인증코드 유효시간 (3분)
        verificationCodeRepository.save(verificationCode);

        try {
            emailService.sendVerificationCode(email, code);
            return ResponseEntity.ok(ApiResponse.onSuccess("해당 이메일로 인증번호를 전송했습니다."));
        } catch (UserHandler e) {
            throw e;
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/verify")
    @Operation(summary = "이메일 인증번호 검증 API",
            description = "이메일로 발송된 인증번호의 일치 여부 검증")
    public ResponseEntity<ApiResponse<String>> verifyCode(@RequestBody @Valid EmailRequestDto.VerifyCodeDto request) {
        // 인증코드 조회
        VerificationCode verificationCode = verificationCodeRepository
                .findTopByEmailOrderByExpirationTimeDesc(request.getEmail())
                .orElseThrow(() -> new UserHandler(ErrorStatus.VERIFICATION_CODE_NOT_SENT));

        // 인증코드 만료 여부 확인
        if (verificationCode.isExpired()) {
            throw new UserHandler(ErrorStatus.VERIFICATION_CODE_EXPIRED);
        }

        // 인증코드 일치 여부 확인
        if (!verificationCode.getCode().equals(request.getCode())) {
            throw new UserHandler(ErrorStatus.VERIFICATION_CODE_MISMATCH);
        }

        return ResponseEntity.ok(ApiResponse.onSuccess("이메일 인증에 성공했습니다."));
    }
}
