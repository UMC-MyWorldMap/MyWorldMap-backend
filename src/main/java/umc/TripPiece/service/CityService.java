package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.TripPiece.converter.CityConverter;
import umc.TripPiece.domain.City;
import umc.TripPiece.domain.Country;
import umc.TripPiece.repository.CityRepository;
import umc.TripPiece.repository.CountryRepository;
import umc.TripPiece.web.dto.request.CityRequestDto;
import umc.TripPiece.web.dto.response.CityResponseDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public List<CityResponseDto.searchDto> searchCity(String keyword){

        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList(); // 빈 리스트를 반환
        }

        List<City> cities = cityRepository.findByNameIgnoreCase(keyword);
        List<Country> countries = countryRepository.findByNameIgnoreCase(keyword);

        List<CityResponseDto.searchDto> searched = new ArrayList<>();

        if (!cities.isEmpty()){
            searched.addAll(cities.stream().map(CityConverter::toSearchDto).toList());
        }

        if(!countries.isEmpty()){
            countries.forEach(country -> {
                List<City> citiesInCountry = cityRepository.findByCountryId(country.getId());
                searched.addAll(citiesInCountry.stream().map(CityConverter::toSearchDto).toList());
            });
        }

        return searched;
    }

}
