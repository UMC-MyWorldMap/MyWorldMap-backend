package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.TripPiece.converter.MapConverter;
import umc.TripPiece.domain.Map;
import umc.TripPiece.repository.MapRepository;
import umc.TripPiece.repository.CityRepository;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.MapResponseDto;
import umc.TripPiece.web.dto.response.MapStatsResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MapService {

    private final MapRepository mapRepository;
    private final CityRepository cityRepository;

    public List<MapResponseDto> getAllMaps() {
        return mapRepository.findAll().stream()
                .map(MapConverter::toMapResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MapResponseDto createMap(MapRequestDto requestDto) {
        Map map = MapConverter.toMap(requestDto);
        Map savedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(savedMap);
    }

    public long countCountriesByUserId(Long userId) {
        return mapRepository.countDistinctCountryCodeByUserId(userId);
    }

    public long countCitiesByUserId(Long userId) {
        return cityRepository.countDistinctCityByUserId(userId);
    }

    public MapStatsResponseDto getMapStats(Long userId) {
        long countryCount = countCountriesByUserId(userId);
        long cityCount = countCitiesByUserId(userId);
        return new MapStatsResponseDto(countryCount, cityCount);
    }
}
