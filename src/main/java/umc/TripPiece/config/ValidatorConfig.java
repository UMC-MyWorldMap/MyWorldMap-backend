package umc.TripPiece.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.domain.TripPiece;
import umc.TripPiece.repository.CityRepository;
import umc.TripPiece.repository.MapRepository;
import umc.TripPiece.repository.TravelRepository;
import umc.TripPiece.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ValidatorConfig {
    @Bean
    public Map<Class<?>, JpaRepository<?, Long>> repositoryMap(
            UserRepository userRepository,
            CityRepository cityRepository,
            MapRepository mapRepository,
            TravelRepository travelRepository
    ) {
        Map<Class<?>, JpaRepository<?, Long>> map = new HashMap<>();
        map.put(umc.TripPiece.domain.User.class, userRepository);
        map.put(umc.TripPiece.domain.City.class, cityRepository);
        map.put(umc.TripPiece.domain.Map.class, mapRepository);
        map.put(umc.TripPiece.domain.Travel.class, travelRepository);
        return map;
    }

    @Bean
    public Map<Class<?>, ErrorStatus> errorStatusMap() {
        Map<Class<?>, ErrorStatus> map = new HashMap<>();
        map.put(umc.TripPiece.domain.User.class, ErrorStatus.NOT_FOUND_USER);
        map.put(umc.TripPiece.domain.City.class, ErrorStatus.NOT_FOUND_CITY);
        map.put(umc.TripPiece.domain.Map.class, ErrorStatus.NOT_FOUND_MAP);
        map.put(umc.TripPiece.domain.Travel.class, ErrorStatus.NOT_FOUND_TRAVEL);
        return map;
    }
}
