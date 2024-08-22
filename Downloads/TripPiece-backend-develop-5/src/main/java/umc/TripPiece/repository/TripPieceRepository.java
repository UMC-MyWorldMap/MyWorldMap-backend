package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.TripPiece.domain.TripPiece;
import umc.TripPiece.domain.enums.Category;

import java.util.List;

public interface TripPieceRepository extends JpaRepository<TripPiece, Long> {
    List<TripPiece> findByTravelId(Long travelId);
    List<TripPiece> findByUserId(Long userId);
    List<TripPiece> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<TripPiece> findByUserIdOrderByCreatedAtAsc(Long userId);
    List<TripPiece> findByUserIdAndCategoryOrCategoryOrderByCreatedAtDesc(Long userId, Category category1, Category category2);
    List<TripPiece> findByUserIdAndCategoryOrCategoryOrderByCreatedAtAsc(Long userId, Category category1, Category category2);

}
