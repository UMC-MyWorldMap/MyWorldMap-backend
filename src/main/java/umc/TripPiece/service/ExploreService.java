package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.converter.ExploreConverter;
import umc.TripPiece.converter.TravelConverter;
import umc.TripPiece.domain.City;
import umc.TripPiece.domain.Country;
import umc.TripPiece.domain.Travel;
import umc.TripPiece.repository.CityRepository;
import umc.TripPiece.repository.CountryRepository;
import umc.TripPiece.repository.TravelRepository;
import umc.TripPiece.web.dto.response.ExploreResponseDto;
import umc.TripPiece.web.dto.response.TravelResponseDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExploreService {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final TravelRepository travelRepository;

    @Transactional
    public List<ExploreResponseDto.ExploreListDto> searchTravels(String query, String sort){
        List<City> cities = cityRepository.findByNameContainingIgnoreCase(query);
        List<Country> countries = countryRepository.findByNameContainingIgnoreCase(query);
        Set<Long> cityIds = new HashSet<>();
        cities.forEach(city -> cityIds.add(city.getId()));

        countries.forEach(country -> {
            List<City> citiesInCountry = cityRepository.findByCountryId(country.getId());
            citiesInCountry.forEach(city -> cityIds.add(city.getId()));
        });

        List<Travel> travels;
        if(sort.equals("latest")) {
            travels = travelRepository.findByCityIdInAndTravelOpenTrueOrderByCreatedAtDesc(new ArrayList<>(cityIds));
        } else {
            travels = travelRepository.findByCityIdInAndTravelOpenTrueOrderByCreatedAtAsc(new ArrayList<>(cityIds));
        }
        return travels.stream().distinct().map(ExploreConverter::toExploreListDto).toList();
    }

    @Transactional
    public List<ExploreResponseDto.PopularCitiesDto> getCitiesByTravelCount() {
       List<City> cities = cityRepository.findAllByOrderByLogCountDesc();
       return cities.stream().limit(8).map(ExploreConverter::toPopularCitiesDto).toList();
    }


}
