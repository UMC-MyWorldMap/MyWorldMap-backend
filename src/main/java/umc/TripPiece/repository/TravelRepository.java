package umc.TripPiece.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.TripPiece.domain.Travel;
import umc.TripPiece.domain.enums.TravelStatus;

import java.math.BigInteger;
import java.util.List;

public interface TravelRepository extends JpaRepository<Travel, Long> {
    List<Travel> findByCityId(Long cityId);
    List<Travel> findByCity_CountryId(Long countryId);
    Optional<Travel> findByStatusAndUserId(TravelStatus travelStatus, Long userId);

    List<Travel> findByUserId(Long userId);

    List<Travel> findByCityIdInAndTravelOpenTrueOrderByCreatedAtDesc(List<Long> cityIds);
    List<Travel> findByCityIdInAndTravelOpenTrueOrderByCreatedAtAsc(List<Long> cityIds);
}
