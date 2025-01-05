package umc.TripPiece.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.UserHandler;

import java.io.IOException;
import java.util.Random;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 생성
        return String.valueOf(code);
    }

    public void sendVerificationCode(String toEmail, String code) throws MessagingException, IOException {
        String subject = "[여행조각(TripPiece)] 회원가입 시 이메일 인증번호 안내드립니다.";

        // HTML 파일을 읽고 코드 삽입
        String content;
        try {
            content = getEmailHtmlContent(code);
            sendEmail(toEmail, subject, content);
        } catch (IOException e) {
            // 이메일 템플릿 로드 실패
            throw new UserHandler(ErrorStatus.EMAIL_TEMPLATE_ERROR);
        } catch (MessagingException e) {
            // 이메일 전송 실패
            throw new UserHandler(ErrorStatus.EMAIL_SEND_FAILED);
        }

    }

    private String getEmailHtmlContent(String code) throws IOException {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email.html");
            StringBuilder content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            // 인증 코드 삽입
            return content.toString().replace("{code}", code);
        } catch (IOException e) {
            // 템플릿 읽기 실패
            throw new IOException("이메일 템플릿 로드 실패", e);
        }
    }

    public void sendEmail(String toEmail, String title, String content) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content, true);
        try {
            emailSender.send(message);
        } catch (RuntimeException e) {
            throw new MessagingException("이메일 전송 실패", e);
        }
    }
}