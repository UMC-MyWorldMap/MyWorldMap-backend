package umc.TripPiece.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.GeneralException;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(request, response);
        } catch (GeneralException e) {
            String message = e.getMessage();

            if (ErrorStatus.INVALID_TOKEN.getMessage().equals(message)) {
                setResponse(response, ErrorStatus.INVALID_TOKEN);
            }
            else if (ErrorStatus.TOKEN_EXPIRED.getMessage().equals(message)) {
                setResponse(response, ErrorStatus.TOKEN_EXPIRED);
            }
            else if (ErrorStatus.UNSUPPORTED_TOKEN.getMessage().equals(message)) {
                setResponse(response, ErrorStatus.UNSUPPORTED_TOKEN);
            }
            else if (ErrorStatus.NOT_FOUND_TOKEN.getMessage().equals(message)) {
                setResponse(response, ErrorStatus.NOT_FOUND_TOKEN);
            }
        }
    }

    private void setResponse(HttpServletResponse response, ErrorStatus errorStatus) throws RuntimeException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorStatus.getHttpStatus().value());
        response.getWriter().print(errorStatus.getMessage());
    }
}
