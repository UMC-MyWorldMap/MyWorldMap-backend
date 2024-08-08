package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.TripPiece.domain.Map;

public interface MapRepository extends JpaRepository<Map, Long> {

    @Query("SELECT COUNT(DISTINCT m.countryCode) FROM Map m WHERE m.id = :userId")
    long countDistinctCountryCodeByUserId(Long userId);

    @Query("SELECT COUNT(DISTINCT t.city) FROM Travel t WHERE t.user.id = :userId")
    long countDistinctCityByUserId(Long userId);
}
