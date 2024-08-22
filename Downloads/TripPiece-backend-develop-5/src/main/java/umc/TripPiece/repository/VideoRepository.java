package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.TripPiece.domain.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
