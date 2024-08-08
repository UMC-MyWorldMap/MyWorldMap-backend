package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.TripPiece.domain.City;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByNameContainingIgnoreCase(String name);
    List<City> findByCountryId(Long countryId);

    @Query("SELECT COUNT(DISTINCT c.id) FROM City c JOIN c.travels t WHERE t.user.id = :userId")
    long countDistinctCityByUserId(Long userId);
}
