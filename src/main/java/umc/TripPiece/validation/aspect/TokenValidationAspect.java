package umc.TripPiece.validation.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.UserRepository;


@Aspect
@Component
@RequiredArgsConstructor
public class TokenValidationAspect {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Before("@annotation(umc.TripPiece.validation.annotation.ValidateToken)")
    public void validateToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null){
            throw new NotFoundHandler(ErrorStatus.REQUEST_INFO_NOT_FOUND);
        }
        HttpServletRequest request = attributes.getRequest();

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BadRequestHandler(ErrorStatus.INVALID_TOKEN);
        }

        String tokenWithoutBearer = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(tokenWithoutBearer);

        UserContext.setUserId(userId);
    }
}
