package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.TripPiece.domain.Emoji;

public interface EmojiRepository extends JpaRepository<Emoji, Long> {
}
