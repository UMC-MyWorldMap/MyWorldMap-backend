package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.TripPiece.domain.Map;

import java.util.List;

public interface MapRepository extends JpaRepository<Map, Long> {

    // 유저 ID로 맵을 조회하는 메소드
    List<Map> findByUserId(Long userId);

    // 유저가 방문한 나라 수를 조회하는 메소드
    @Query("SELECT COUNT(DISTINCT m.countryCode) FROM Map m WHERE m.userId = :userId")
    long countDistinctCountryCodeByUserId(Long userId);

    // 유저가 방문한 나라의 countryCode 리스트를 조회하는 메소드
    @Query("SELECT DISTINCT m.countryCode FROM Map m WHERE m.userId = :userId")
    List<String> findDistinctCountryCodesByUserId(Long userId);

    // 유저가 방문한 도시의 city.id 리스트를 조회하는 메소드
    @Query("SELECT DISTINCT m.city.id FROM Map m WHERE m.userId = :userId")
    List<Long> findDistinctCityIdsByUserId(Long userId);

    // 유저가 방문한 도시 수를 조회하는 메소드
    @Query("SELECT COUNT(DISTINCT m.city.id) FROM Map m WHERE m.userId = :userId")
    long countDistinctCityByUserId(Long userId);

    // 유저 ID와 국가 코드로 맵을 조회하는 메소드 (마커 반환을 위한 사용)
    Map findByCountryCodeAndUserId(String countryCode, Long userId);
}
