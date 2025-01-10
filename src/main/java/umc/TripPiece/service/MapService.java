package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.TripPiece.converter.MapConverter;
import umc.TripPiece.domain.City;
import umc.TripPiece.domain.Country;
import umc.TripPiece.domain.Map;
import umc.TripPiece.domain.Travel;
import umc.TripPiece.domain.User;
import umc.TripPiece.domain.enums.Color;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.*;
import umc.TripPiece.web.dto.request.MapRequestDto;
import umc.TripPiece.web.dto.response.MapResponseDto;
import umc.TripPiece.web.dto.response.MapStatsResponseDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MapService {

    private final MapRepository mapRepository;
    private final CityRepository cityRepository;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    // 유저별 맵 리스트를 조회
    public List<MapResponseDto> getMapsByUserId(Long userId) {
        return mapRepository.findByUserId(userId).stream()
                .map(MapConverter::toMapResponseDto)
                .collect(Collectors.toList());
    }

    // 맵 생성 시 도시 정보 포함
    @Transactional
    public MapResponseDto createMapWithCity(MapRequestDto requestDto) {
        City city = cityRepository.findById(requestDto.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("City not found with id: " + requestDto.getCityId()));

        Map map = MapConverter.toMap(requestDto, city);
        Map savedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(savedMap);
    }

    // 맵 색상 수정
    @Transactional
    public MapResponseDto updateMapColor(Long mapId, String newColor) {
        Map map = mapRepository.findById(mapId)
                .orElseThrow(() -> new IllegalArgumentException("Map not found with id: " + mapId));

        map.setColor(newColor); // 요청된 hex 값을 설정
        Map updatedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(updatedMap);
    }

    // 맵 색상 삭제
    @Transactional
    public void deleteMapColor(Long mapId) {
        Map map = mapRepository.findById(mapId)
                .orElseThrow(() -> new IllegalArgumentException("Map not found with id: " + mapId));

        map.setColor(null); // 색상 삭제
        mapRepository.save(map);
    }

    // 여러 색상 선택 업데이트
    @Transactional
    public MapResponseDto updateMultipleMapColors(Long mapId, List<String> colorStrings) {
        Map map = mapRepository.findById(mapId)
                .orElseThrow(() -> new IllegalArgumentException("Map not found with id: " + mapId));

        map.setColors(colorStrings); // 다중 색상 설정
        Map updatedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(updatedMap);
    }

    // 유저별 방문한 나라와 도시 통계 반환
    public MapStatsResponseDto getMapStatsByUserId(Long userId) {
        long countryCount = mapRepository.countDistinctCountryCodeByUserId(userId);
        long cityCount = mapRepository.countDistinctCityByUserId(userId);

        List<String> countryCodes = mapRepository.findDistinctCountryCodesByUserId(userId);
        List<Long> cityIds = mapRepository.findDistinctCityIdsByUserId(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return new MapStatsResponseDto(
                countryCount,
                cityCount,
                countryCodes,
                cityIds,
                user.getProfileImg(),
                user.getNickname()
        );
    }

    // 방문한 나라 누적 정보 반환
    public List<String> getVisitedCountries(Long userId) {
        return mapRepository.findDistinctCountryCodesByUserId(userId);
    }

    public long getVisitedCountryCount(Long userId) {
        return mapRepository.countDistinctCountryCodeByUserId(userId);
    }

    // 방문한 나라 누적 정보와 프로필 통합 반환
    public MapStatsResponseDto getVisitedCountriesWithProfile(Long userId) {
        List<String> visitedCountries = getVisitedCountries(userId);
        long visitedCountryCount = getVisitedCountryCount(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return new MapStatsResponseDto(
                visitedCountryCount,
                visitedCountries.size(),
                visitedCountries,
                Collections.emptyList(), // 도시 ID 리스트는 필요 시 추가
                user.getProfileImg(),
                user.getNickname()
        );
    }

    // 도시 및 국가 검색
    public List<MapResponseDto.searchDto> searchCitiesCountry(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<City> cities = cityRepository.findByNameIgnoreCase(keyword);
        List<Country> countries = countryRepository.findByNameIgnoreCase(keyword);

        List<MapResponseDto.searchDto> results = new ArrayList<>();

        if (!cities.isEmpty()) {
            results.addAll(cities.stream().map(MapConverter::toSearchDto).collect(Collectors.toList()));
        }

        if (!countries.isEmpty()) {
            for (Country country : countries) {
                List<City> citiesInCountry = cityRepository.findByCountryId(country.getId());
                results.addAll(citiesInCountry.stream().map(MapConverter::toSearchDto).collect(Collectors.toList()));
            }
        }

        return results;
    }

    // 마커 반환
    public List<MapResponseDto.getMarkerResponse> getMarkers(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<Map> maps = mapRepository.findByUserId(userId);

        return maps.stream()
                .map(map -> MapConverter.toMarkerResponseDto(
                        map,
                        "", // 마커 이미지 URL 추가 필요 시 수정
                        map.getCity().getCountry().getName(),
                        map.getCity().getName()
                ))
                .collect(Collectors.toList());
    }
}
