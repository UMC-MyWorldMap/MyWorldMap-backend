package umc.TripPiece.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.TripPiece.domain.City;
import umc.TripPiece.domain.Country;

import java.math.BigInteger;
import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {
    List<Country> findByNameIgnoreCase(String name);

    @Query("SELECT c FROM Country c WHERE :query LIKE CONCAT('%', c.name, '%')")
    List<Country> findCountriesInSearch(@Param("query") String query);
}
