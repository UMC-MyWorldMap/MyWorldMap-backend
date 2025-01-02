package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.aws.s3.AmazonS3Manager;
import umc.TripPiece.converter.TravelConverter;
import umc.TripPiece.converter.TripPieceConverter;
import umc.TripPiece.domain.*;
import umc.TripPiece.domain.enums.Category;

import umc.TripPiece.domain.enums.Category;
import umc.TripPiece.domain.enums.TravelStatus;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.*;
import umc.TripPiece.validation.aspect.UserContext;
import umc.TripPiece.web.dto.request.TravelRequestDto;
import umc.TripPiece.web.dto.response.TravelResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

        TripPiece newTripPiece = TravelConverter.toTripPieceMemo(request, user);
        newTripPiece.setTravel(travelRepository.findById(travelId).get());
        newTripPiece.setCategory(Category.MEMO);

        Travel travel = travelRepository.findById(travelId).get();
        travel.setMemoNum(travel.getMemoNum()+1);

        return tripPieceRepository.save(newTripPiece);
    }

    @Transactional
    public TripPiece createEmoji(Long travelId, List<String> emojis, TravelRequestDto.MemoDto request, String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

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
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

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
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

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
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));;

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
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

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
    public TravelResponseDto.Create createTravel(TravelRequestDto.Create request, MultipartFile thumbnail) {
        Long userId = UserContext.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundHandler(ErrorStatus.NOT_FOUND_USER));
        City city = cityRepository.findByNameContainingIgnoreCase(request.getCityName()).stream().findFirst().orElseThrow(() -> new NotFoundHandler(ErrorStatus.NOT_FOUND_CITY));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestHandler(ErrorStatus.INVALID_TRAVEL_DATE);
        }
        if (request.getTitle().isEmpty()) {
            throw new BadRequestHandler(ErrorStatus.INVALID_TRAVEL_TITLE);
        }

        String uuid = UUID.randomUUID().toString();
        String thumbnailUrl = s3Manager.uploadFile("thumbnails/" + uuid, thumbnail);

        Travel OngoingTravel = travelRepository.findByStatusAndUserId(TravelStatus.ONGOING, userId);
        if(OngoingTravel != null) {
            throw new BadRequestHandler(ErrorStatus.TRAVEL_INPROGRESS);
        }

        Travel travel = TravelConverter.toTravel(request, city);
        travel.setUser(user);
        travel.setThumbnail(thumbnailUrl);
        travel.setStatus(TravelStatus.ONGOING);

        Travel savedTravel = travelRepository.save(travel);
        return TravelConverter.toCreateResponseDto(savedTravel);
    }

    @Transactional
    public TravelResponseDto.TripSummaryDto endTravel(Long travelId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow();
        if (travel.getStatus() == TravelStatus.COMPLETED) {
            throw new BadRequestHandler(ErrorStatus.TRAVEL_COMPLETED);
        }
        travel.setStatus(TravelStatus.COMPLETED);

        City city = travel.getCity();
        city.setLogCount(city.getLogCount() + 1);

        List<TripPiece> tripPieces = tripPieceRepository.findByTravelId(travelId);
        initPicturesThumbnail(travel);

        return TravelConverter.toTripSummary(travel, tripPieces);
    }

    @Transactional
    public List<TravelResponseDto.TripPieceSummaryDto> continueTravel(Long travelId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));
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
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<Travel> travels = travelRepository.findByUserId(userId);
        return travels.stream().map(TravelConverter::toTravelListDto).collect(Collectors.toList());
    }

    @Transactional
    public TravelResponseDto.TripSummaryDto getTravelDetails(Long travelId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));
        List<TripPiece> tripPieces = tripPieceRepository.findByTravelId(travelId);
        return TravelConverter.toTripSummary(travel, tripPieces);
    }

    @Transactional
    public TravelResponseDto.getOngoingTravelResultDto getOngoingTravel(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("user not found"));
        Travel travel = travelRepository.findByStatusAndUserId(TravelStatus.ONGOING, userId);
        City city = travel.getCity();
        Country country = city.getCountry();

        String nickname = user.getNickname();
        String profileImg = user.getProfileImg();
        String countryName = country.getName();

        LocalDateTime startDate = travel.getStartDate();
        LocalDateTime today = LocalDateTime.now();
        Long dayCount = ChronoUnit.DAYS.between(startDate, today);


        return TravelConverter.toOngoingTravelResultDto(travel, nickname, profileImg, countryName, dayCount);
    }

    @Transactional
    public List<TravelResponseDto.UpdatablePictureDto> getPictureResponses(Long travelId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));

        return getPictures(travel).stream()
                .map(TravelConverter::toUpdatablePictureDto)
                .toList();
    }

    @Transactional
    public List<TravelResponseDto.UpdatablePictureDto> getThumbnailPictures(Long travelId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("travel not found"));

        // index 순으로 List 반환
        return getPictures(travel).stream()
                .filter(picture -> picture.getTravel_thumbnail().equals(true))
                .sorted(Comparator.comparingInt(Picture::getThumbnail_index))
                .map(TravelConverter::toUpdatablePictureDto)
                .toList();
    }

    @Transactional
    public List<TravelResponseDto.UpdatablePictureDto> updateThumbnail(Long travelId, List<Long> pictureIdList) {
        if (pictureIdList.size() != 9) throw new IllegalArgumentException("리스트의 크기는 9여야 합니다.");

        Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 여행기입니다."));

        List<Picture> pictures = pictureIdList.stream()
                .map(id -> {
                    // id = -1이면 null 객체를 담음
                    if (id == -1) return null;

                    Picture picture = pictureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("사진을 찾을 수 없습니다. (id = %d)", id)));
                    if (!getPictures(travel).contains(picture)) throw new IllegalArgumentException(String.format("해당 여행기에 존재하지 않는 사진입니다. (id = %d)", picture.getId()));

                    return picture;
                })
                .toList();

        for (int i = 0; i < 9; i++) {
            int tempI = i;

            // 기존의 사진
            Picture originPicture = getPictures(travel).stream()
                    .filter(picture -> picture.getThumbnail_index().equals(tempI + 1))
                    .findFirst().orElse(null);

            // 새로 설정할 사진
            Picture newPicture = pictures.get(i);

            // 같은 객체이면 해당 위치의 썸네일 유지
            if (originPicture != null && originPicture.equals(newPicture)) continue;

            // 기존의 사진을 썸네일에서 해제
            if (originPicture != null) {
                originPicture.setTravel_thumbnail(false);
                originPicture.setThumbnail_index(0);
            }

            // 새 사진이 null 객체라면 해제 후 로직 종료
            if (newPicture == null) {
                continue;
            }

            // 새 사진이 다른 객체이면 썸네일로 지정
            newPicture.setTravel_thumbnail(true);
            newPicture.setThumbnail_index(i + 1);
        }

        return pictures.stream()
                .filter(Objects::nonNull)
                .map(TravelConverter::toUpdatablePictureDto)
                .toList();
    }

    private void initPicturesThumbnail(Travel travel) {
        List<Picture> pictures = getPictures(travel);
        int thumbnailIndex = 1;

        // 여행 종료 시 썸네일 랜덤 지정
        while (isThumbnailAvailable(travel)) {
            int randomIndex = (int) (Math.random() * pictures.size());
            Picture picture = pictures.get(randomIndex);
            picture.setTravel_thumbnail(true);
            picture.setThumbnail_index(thumbnailIndex);
            pictures.remove(picture);
            thumbnailIndex++;

            // 사진이 9장보다 적을 때, 다 설정이 되면 메서드 종료
            if (pictures.isEmpty()) return;
        }
    }

    private boolean isThumbnailAvailable(Travel travel) {
        List<Picture> pictures = getPictures(travel);

        return pictures.stream()
                .filter(Picture::getTravel_thumbnail)
                .count() < 9;
    }

    private List<Picture> getPictures(Travel travel) {
        List<Picture> pictures = new ArrayList<>();

        travel.getTripPieces().stream()
                .filter(tripPiece -> tripPiece.getCategory() == Category.PICTURE || tripPiece.getCategory() == Category.SELFIE)
                .forEach(tripPiece -> {
                    List<Picture> pictureList = tripPiece.getPictures();
                    pictures.addAll(pictureList);
                });

        return pictures;
    }
}
