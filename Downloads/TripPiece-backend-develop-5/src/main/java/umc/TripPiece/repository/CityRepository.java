package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.TripPiece.domain.City;

import java.math.BigInteger;
import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByNameContainingIgnoreCase(String name);
    List<City> findByCountryId(Long countryId);

    @Query("SELECT COUNT(DISTINCT t.city) FROM Travel t WHERE t.user.id = :userId")
    long countDistinctCityByUserId(Long userId);

    // 유저가 방문한 도시 리스트 조회
    @Query("SELECT DISTINCT c FROM City c JOIN Travel t ON t.city.id = c.id WHERE t.user.id = :userId")
    List<City> findCitiesByUserId(Long userId);
}
