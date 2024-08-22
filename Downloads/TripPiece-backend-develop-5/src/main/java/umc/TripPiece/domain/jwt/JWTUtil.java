package umc.TripPiece.domain.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import umc.TripPiece.domain.User;
import umc.TripPiece.repository.UserRepository;

@Component
public class JWTUtil {

    private final TokenProvider tokenProvider;
    private final JWTProperties jwtProperties;
    private final UserRepository userRepository;

    @Autowired
    public JWTUtil(TokenProvider tokenProvider, JWTProperties jwtProperties, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
    }

    public String createAccessToken(String email) {
        return tokenProvider.createToken(email, jwtProperties.getAccessTokenValidity());
    }

    public String createRefreshToken(String email) {
        return tokenProvider.createToken(email, jwtProperties.getRefreshTokenValidity());
    }

    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    public String getEmailFromToken(String token) {
        return tokenProvider.getEmailFromToken(token);
    }

    public Long getUserIdFromToken(String token) {
        String email = getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 계정입니다.")
        );
        return user.getId();
    }

}
