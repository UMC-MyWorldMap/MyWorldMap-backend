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
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    // 유저별 맵 리스트를 조회하는 메소드
    public List<MapResponseDto> getMapsByUserId(Long userId) {
        return mapRepository.findByUserId(userId).stream()
                .map(MapConverter::toMapResponseDto)
                .collect(Collectors.toList());
    }

    // 맵 생성 시 선택한 도시 정보도 함께 저장하는 메소드
    @Transactional
    public MapResponseDto createMapWithCity(MapRequestDto requestDto) {
        City city = cityRepository.findById(requestDto.getCityId()).orElseThrow(() ->
                new IllegalArgumentException("City not found with id: " + requestDto.getCityId()));
        Map map = MapConverter.toMap(requestDto, city);
        Map savedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(savedMap);
    }

    // 유저별 방문한 나라와 도시 통계를 반환하는 메소드
    public MapStatsResponseDto getMapStatsByUserId(Long userId) {
        long countryCount = mapRepository.countDistinctCountryCodeByUserId(userId);
        long cityCount = mapRepository.countDistinctCityByUserId(userId);

        List<String> countryCodes = mapRepository.findDistinctCountryCodesByUserId(userId);
        List<Long> cityIds = mapRepository.findDistinctCityIdsByUserId(userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String profileImg = user.getProfileImg();
        String nickname = user.getNickname();

        return new MapStatsResponseDto(countryCount, cityCount, countryCodes, cityIds, profileImg, nickname);
    }

    // 마커 반환 기능
    @Transactional
    public List<MapResponseDto.getMarkerResponse> getMarkers(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<Travel> travels = travelRepository.findByUserId(userId);
        List<MapResponseDto.getMarkerResponse> markers = new ArrayList<>();

        for (Travel travel : travels) {
            City city = travel.getCity();
            Country country = city.getCountry();
            Map map = mapRepository.findByCountryCodeAndUserId(country.getCode(), userId);
            MapResponseDto.getMarkerResponse marker = MapConverter.toMarkerResponseDto(map, travel.getThumbnail(), country.getName(), city.getName());
            markers.add(marker);
        }

        return markers;
    }

    // 맵 색상 수정 메서드
    @Transactional
    public MapResponseDto updateMapColor(Long mapId, String newColor) {
        Map map = mapRepository.findById(mapId)
                .orElseThrow(() -> new IllegalArgumentException("Map not found with id: " + mapId));
        Color color = Color.valueOf(newColor);  // 문자열을 Color 객체로 변환
        map.setColor(color);  // Color 객체 설정
        Map updatedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(updatedMap);
    }

    // 맵 색상 삭제 메서드
    @Transactional
    public void deleteMapColor(Long mapId) {
        Map map = mapRepository.findById(mapId)
                .orElseThrow(() -> new IllegalArgumentException("Map not found with id: " + mapId));
        map.setColor(null); // 색상 삭제
        mapRepository.save(map);
    }

    // 여러 색상 선택 메서드
    @Transactional
    public MapResponseDto updateMultipleMapColors(Long mapId, List<String> colorStrings) {
        Map map = mapRepository.findById(mapId)
                .orElseThrow(() -> new IllegalArgumentException("Map not found with id: " + mapId));
        List<Color> colors = colorStrings.stream()
                .map(Color::valueOf)  // 각 문자열을 Color로 변환
                .collect(Collectors.toList());
        map.setColors(colors);  // 다중 색상 설정
        Map updatedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(updatedMap);
    }

    public List<MapResponseDto.searchDto> searchCitiesCountry(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList(); // 빈 리스트를 반환
        }

        List<City> cities = cityRepository.findByNameIgnoreCase(keyword);
        List<Country> countries = countryRepository.findByNameIgnoreCase(keyword);

        List<MapResponseDto.searchDto> searched = new ArrayList<>();

        if (!cities.isEmpty()){
            searched.addAll(cities.stream().map(MapConverter::toSearchDto).toList());
        }

        if(!countries.isEmpty()){
            countries.forEach(country -> {
                List<City> citiesInCountry = cityRepository.findByCountryId(country.getId());
                searched.addAll(citiesInCountry.stream().map(MapConverter::toSearchDto).toList());
            });
        }

        return searched;
    }
}
