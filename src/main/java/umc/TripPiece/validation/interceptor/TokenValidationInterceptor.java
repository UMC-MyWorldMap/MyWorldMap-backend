package umc.TripPiece.validation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.UserRepository;
import umc.TripPiece.validation.aspect.UserContext;

@Component
@RequiredArgsConstructor
public class TokenValidationInterceptor implements HandlerInterceptor {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BadRequestHandler(ErrorStatus.INVALID_TOKEN);
        }

        String tokenWithoutBearer = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(tokenWithoutBearer);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_USER);
        }
        UserContext.setUserId(userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
