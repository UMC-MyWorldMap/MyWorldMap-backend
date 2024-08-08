package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.TripPiece.domain.Uuid;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
