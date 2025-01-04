package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.TripPiece.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByEmailAndProviderId(String email, Long providerId);
    Optional<User> findByProviderId(Long providerId);
}
