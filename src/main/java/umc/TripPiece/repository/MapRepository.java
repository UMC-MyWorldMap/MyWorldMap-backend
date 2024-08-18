package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.TripPiece.domain.Map;

import java.util.List;

public interface MapRepository extends JpaRepository<Map, Long> {

    // 특정 유저 ID의 맵 정보를 조회
    List<Map> findByUserId(Long userId);

    // 유저 ID별 나라 수 계산
    @Query("SELECT COUNT(DISTINCT m.countryCode) FROM Map m WHERE m.userId = :userId")
    long countDistinctCountryCodeByUserId(Long userId);

    // 유저 ID별 도시 수 계산
    @Query("SELECT COUNT(DISTINCT t.city) FROM Travel t WHERE t.user.id = :userId")
    long countDistinctCityByUserId(Long userId);

    // 특정 유저의 국가 코드와 유저 ID로 맵 정보를 조회
    Map findByCountryCodeAndUserId(String countryCode, Long userId);
}
