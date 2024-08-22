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
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.MapRepository;
import umc.TripPiece.repository.CityRepository;
import umc.TripPiece.repository.TravelRepository;
import umc.TripPiece.repository.UserRepository;
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
    private final JWTUtil jwtUtil;
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;  // UserRepository 추가

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
        Map map = MapConverter.toMap(requestDto, city);  // City 정보를 함께 전달
        Map savedMap = mapRepository.save(map);
        return MapConverter.toMapResponseDto(savedMap);
    }

    // 유저별 방문한 나라와 도시 통계를 반환하는 메소드
    public MapStatsResponseDto getMapStatsByUserId(Long userId) {
        // 방문한 나라와 도시의 수 계산
        long countryCount = mapRepository.countDistinctCountryCodeByUserId(userId);
        long cityCount = mapRepository.countDistinctCityByUserId(userId);

        // 방문한 나라의 countryCode 리스트 및 cityId 리스트 조회
        List<String> countryCodes = mapRepository.findDistinctCountryCodesByUserId(userId);
        List<Long> cityIds = mapRepository.findDistinctCityIdsByUserId(userId);

        // 유저 정보 (프로필 이미지, 닉네임)
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String profileImg = user.getProfileImg();
        String nickname = user.getNickname();

        // DTO 반환
        return new MapStatsResponseDto(countryCount, cityCount, countryCodes, cityIds, profileImg, nickname);
    }

    // 마커 반환 기능 (기존 유지)
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
}