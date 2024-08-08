package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.TripPiece.domain.TripPiece;
import umc.TripPiece.domain.enums.Category;

import java.util.List;

public interface TripPieceRepository extends JpaRepository<TripPiece, Long> {
    List<TripPiece> findByTravelId(Long travelId);
    List<TripPiece> findByuser_idOrderByCreatedAtDesc(Long user_id);
    List<TripPiece> findByuser_idOrderByCreatedAtAsc(Long user_id);
    List<TripPiece> findByuser_idAndCategoryOrCategoryOrderByCreatedAtDesc(Long user_id, Category category1, Category category2);
    List<TripPiece> findByuser_idAndCategoryOrCategoryOrderByCreatedAtAsc(Long user_id, Category category1, Category category2);

}
