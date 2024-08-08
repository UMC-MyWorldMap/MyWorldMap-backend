package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.aws.s3.AmazonS3Manager;
import umc.TripPiece.converter.TravelConverter;
import umc.TripPiece.converter.TripPieceConverter;
import umc.TripPiece.domain.*;
import umc.TripPiece.domain.enums.Category;
import umc.TripPiece.domain.enums.TravelStatus;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.*;
import umc.TripPiece.web.dto.request.TravelRequestDto;
import umc.TripPiece.web.dto.response.TravelResponseDto;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelService {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final TravelRepository travelRepository;
    private final TripPieceRepository tripPieceRepository;
    private final EmojiRepository emojiRepository;
    private final PictureRepository pictureRepository;
    private final VideoRepository videoRepository;
    private final UuidRepository uuidRepository;
    private final UserRepository userRepository;
    private final AmazonS3Manager s3Manager;
    private final JWTUtil jwtUtil;

    public List<Travel> searchByKeyword(String keyword) {
        List<City> cities = cityRepository.findByNameContainingIgnoreCase(keyword);
        List<Country> countries = countryRepository.findByNameContainingIgnoreCase(keyword);

        List<Travel> travels = new ArrayList<>();

        if(!cities.isEmpty()) {
            travels.addAll(cities.stream().flatMap(city -> travelRepository.findByCityId(city.getId()).stream()).toList());
        }
        if(!countries.isEmpty()) {
            travels.addAll(countries.stream().flatMap(country -> travelRepository.findByCity_CountryId(country.getId()).stream()).toList());
        }
        return travels;
    }

    @Transactional
    public TripPiece createMemo(Long travelId, TravelRequestDto.MemoDto request, String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("user not found"));

        TripPiece newTripPiece = TravelConverter.toTripPieceMemo(request, user);
        newTripPiece.setTravel(travelRepository.findById(travelId).get());
        newTripPiece.setCategory(Category.MEMO);

        Travel travel = travelRepository.findById(travelId).get();
        travel.setMemoNum(travel.getMemoNum()+1);

        return tripPieceRepository.save(newTripPiece);
    }

    @Transactional
    public TripPiece createEmoji(Long travelId, List<String> emojis, TravelRequestDto.MemoDto request, String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("user not found"));

        TripPiece newTripPiece = TravelConverter.toTripPieceMemo(request, user);
        newTripPiece.setTravel(travelRepository.findById(travelId).get());
        newTripPiece.setCategory(Category.EMOJI);

        Travel travel = travelRepository.findById(travelId).get();
        travel.setMemoNum(travel.getMemoNum()+1);

        for(String emoji : emojis) {
            Emoji newEmoji = TripPieceConverter.toTripPieceEmoji(emoji, newTripPiece);
            emojiRepository.save(newEmoji);
        }

        return tripPieceRepository.save(newTripPiece);
    }

    @Transactional
    public TripPiece createPicture(Long travelId, List<MultipartFile> pictures, TravelRequestDto.MemoDto request, String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("user not found"));

        TripPiece newTripPiece = TravelConverter.toTripPieceMemo(request, user);
        newTripPiece.setTravel(travelRepository.findById(travelId).get());
        newTripPiece.setCategory(Category.PICTURE);

        Travel travel = travelRepository.findById(travelId).get();
        travel.setPictureNum(travel.getPictureNum()+1);

        int pictureNum = pictures.size();

        List<Uuid> uuids = new ArrayList<>();

        for(int i = 0; i < pictureNum; i++) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());
            uuids.add(savedUuid);
        }

        List<String> pictureUrls = s3Manager.saveFiles(s3Manager.generateTripPieceKeyNames(uuids), pictures);

        for(int i = 0; i < pictureNum; i++) {
            Picture newPicture = TripPieceConverter.toTripPiecePicture(pictureUrls.get(i), newTripPiece);
            pictureRepository.save(newPicture);
        }

        return tripPieceRepository.save(newTripPiece);

    }

    @Transactional
    public TripPiece createSelfie(Long travelId, MultipartFile picture, TravelRequestDto.MemoDto request, String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("user not found"));

        TripPiece newTripPiece = TravelConverter.toTripPieceMemo(request, user);
        newTripPiece.setTravel(travelRepository.findById(travelId).get());
        newTripPiece.setCategory(Category.SELFIE);

        Travel travel = travelRepository.findById(travelId).get();
        travel.setPictureNum(travel.getPictureNum()+1);

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String pictureUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), picture);

        Picture newPicture = TripPieceConverter.toTripPiecePicture(pictureUrl, newTripPiece);

        pictureRepository.save(newPicture);

        return tripPieceRepository.save(newTripPiece);

    }

    @Transactional
    public TripPiece createVideo(Long travelId, MultipartFile video, TravelRequestDto.MemoDto request, String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("user not found"));;

        TripPiece newTripPiece = TravelConverter.toTripPieceMemo(request, user);
        newTripPiece.setTravel(travelRepository.findById(travelId).get());
        newTripPiece.setCategory(Category.VIDEO);

        Travel travel = travelRepository.findById(travelId).get();
        travel.setVideoNum(travel.getVideoNum()+1);

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String videoUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), video);

        Video newVideo = TripPieceConverter.toTripPieceVideo(videoUrl, newTripPiece);

        videoRepository.save(newVideo);

        return tripPieceRepository.save(newTripPiece);

    }

    @Transactional
    public TripPiece createWhere(Long travelId, MultipartFile video, TravelRequestDto.MemoDto request, String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("user not found"));

        TripPiece newTripPiece = TravelConverter.toTripPieceMemo(request, user);
        newTripPiece.setTravel(travelRepository.findById(travelId).get());
        newTripPiece.setCategory(Category.WHERE);

        Travel travel = travelRepository.findById(travelId).get();
        travel.setVideoNum(travel.getVideoNum()+1);

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String videoUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), video);

        Video newVideo = TripPieceConverter.toTripPieceVideo(videoUrl, newTripPiece);

        videoRepository.save(newVideo);

        return tripPieceRepository.save(newTripPiece);

    }

    @Transactional
    public TravelResponseDto.Create createTravel(TravelRequestDto.Create request, MultipartFile thumbnail, String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("user not found"));

        City city = cityRepository.findByNameContainingIgnoreCase(request.getCityName()).stream().findFirst().orElseThrow(() -> new RuntimeException("city not found"));

        String uuid = UUID.randomUUID().toString();
        String thumbnailUrl = s3Manager.uploadFile("thumbnails/" + uuid, thumbnail);

        Travel travel = TravelConverter.toTravel(request, city);
        travel.setUser(user);
        travel.setThumbnail(thumbnailUrl);
        travel.setStatus(TravelStatus.ONGOING);

        Travel savedTravel = travelRepository.save(travel);
        return TravelConverter.toCreateResponseDto(savedTravel);
    }

    @Transactional
    public TravelResponseDto.TripSummaryDto endTravel(Long travelId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new RuntimeException("travel not found"));
        travel.setStatus(TravelStatus.COMPLETED);
        List<TripPiece> tripPieces = tripPieceRepository.findByTravelId(travelId);
        return TravelConverter.toTripSummary(travel, tripPieces);
    }

    @Transactional
    public List<TravelResponseDto.TripPieceSummaryDto> continueTravel(Long travelId) {
        //Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new RuntimeException("travel not found"));
        List<TripPiece> tripPieces = tripPieceRepository.findByTravelId(travelId);

        java.util.Map<LocalDate, List<TripPiece>> tripPiecesByDate = tripPieces.stream()
                .collect(Collectors.groupingBy(tp -> tp.getCreatedAt().toLocalDate()));
        return tripPiecesByDate.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .sorted(Comparator.comparing(TripPiece::getCreatedAt))
                        .limit(2)
                        .map(TravelConverter::toTripPieceSummary))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TravelResponseDto.TravelListDto> getTravelList(String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        List<Travel> travels = travelRepository.findByUserId(user_id);
        return travels.stream().map(TravelConverter::toTravelListDto).collect(Collectors.toList());
    }

    @Transactional
    public TravelResponseDto.TripSummaryDto getTravelDetails(Long travelId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new RuntimeException("travel not found"));
        List<TripPiece> tripPieces = tripPieceRepository.findByTravelId(travelId);
        return TravelConverter.toTripSummary(travel, tripPieces);
    }

    @Transactional
    public TravelResponseDto.getOngoingTravelResultDto getOngoingTravel(String token) {
        Long user_id = jwtUtil.getuser_idFromToken(token);
        Travel travel = travelRepository.findByStatusAndUserId(TravelStatus.ONGOING, user_id);
        return TravelConverter.toOngoingTravelResultDto(travel);
    }
}
