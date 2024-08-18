package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.TripPiece.converter.MapConverter;
import umc.TripPiece.domain.City;
import umc.TripPiece.domain.Country;
import umc.TripPiece.domain.Map;
import umc.TripPiece.domain.Travel;
import umc.TripPiece.repository.MapRepository;
import umc.TripPiece.repository.CityRepository;
import umc.TripPiece.repository.TravelRepository;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.MapResponseDto;
import umc.TripPiece.web.dto.response.MapStatsResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MapService {

    private final MapRepository mapRepository;
    private final CityRepository cityRepository;
    private final TravelRepository travelRepository;

    // 특정 유저의 모든 맵 정보를 조회하는 메서드
    public List<MapResponseDto> getMapsByUserId(Long userId) {
        return mapRepository.findByUserId(userId).stream()
                .map(MapConverter::toMapResponseDto)
                .collect(Collectors.toList());
    }

    // 맵을 생성하는 메서드
    @Transactional
    public MapResponseDto createMap(MapRequestDto requestDto) {
        Map map = MapConverter.toMap(requestDto);
        Map savedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(savedMap);
    }

    // 유저 ID별 방문한 나라 수 계산 메서드
    public long countCountriesByUserId(Long userId) {
        return mapRepository.countDistinctCountryCodeByUserId(userId);
    }

    // 유저 ID별 방문한 도시 수 계산 메서드
    public long countCitiesByUserId(Long userId) {
        return cityRepository.countDistinctCityByUserId(userId);
    }

    // 유저 ID별 나라/도시 수 통계 조회 메서드
    public MapStatsResponseDto getMapStatsByUserId(Long userId) {
        long countryCount = countCountriesByUserId(userId);
        long cityCount = countCitiesByUserId(userId);
        return new MapStatsResponseDto(countryCount, cityCount);
    }

    // 특정 유저의 마커 정보를 반환하는 메서드
    @Transactional
    public List<MapResponseDto.MarkerResponse> getMarkersByUserId(Long userId) {
        List<Travel> travels = travelRepository.findByUserId(userId);
        List<MapResponseDto.MarkerResponse> markers = new ArrayList<>();

        for (Travel travel : travels) {
            City city = travel.getCity();
            Country country = city.getCountry();
            Map map = mapRepository.findByCountryCodeAndUserId(country.getCode(), userId);
            MapResponseDto.MarkerResponse marker = new MapResponseDto.MarkerResponse(
                    country.getName(), city.getName(), travel.getThumbnail());
            markers.add(marker);
        }

        return markers;
    }
}
