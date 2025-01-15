package umc.TripPiece.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.aws.s3.AmazonS3Manager;
import umc.TripPiece.converter.TripPieceConverter;
import umc.TripPiece.domain.*;
import umc.TripPiece.domain.enums.Category;
import umc.TripPiece.repository.EmojiRepository;
import umc.TripPiece.repository.PictureRepository;
import umc.TripPiece.repository.TripPieceRepository;
import umc.TripPiece.repository.UuidRepository;
import umc.TripPiece.repository.VideoRepository;
import umc.TripPiece.security.SecurityUtils;
import umc.TripPiece.web.dto.request.TripPieceRequestDto;
import umc.TripPiece.web.dto.response.TripPieceResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TripPieceService {

    private final TripPieceRepository tripPieceRepository;
    private final EmojiRepository emojiRepository;
    private final PictureRepository pictureRepository;
    private final VideoRepository videoRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    @Transactional
    public List<TripPieceResponseDto.TripPieceListDto> getTripPieceList(String sort) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 정렬 기준에 따라 데이터를 조회
        List<TripPiece> tripPieces = "earliest".equalsIgnoreCase(sort)
                ? tripPieceRepository.findByUserIdOrderByCreatedAtAsc(userId)
                : tripPieceRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();

        for (TripPiece tripPiece : tripPieces) {
            Travel travel = tripPiece.getTravel();
            City city = travel.getCity();
            Country country = city.getCountry();
            Category category = tripPiece.getCategory();

            TripPieceResponseDto.TripPieceListDto tripPieceListDto = new TripPieceResponseDto.TripPieceListDto();

            if (category == Category.MEMO)
            {
                tripPieceListDto.setCategory(Category.MEMO);
                tripPieceListDto.setMemo(tripPiece.getDescription());
            }
            else if (category == Category.EMOJI)
            {
                List<Emoji> emojis = tripPiece.getEmojis();

                tripPieceListDto.setCategory(Category.MEMO);
                tripPieceListDto.setMemo(emojis.get(0).getEmoji() + emojis.get(1).getEmoji() + emojis.get(2).getEmoji() + emojis.get(3).getEmoji());
            }
            else if (category == Category.PICTURE || category == Category.SELFIE)
            {
                // 여러개 사진이 있다면, 썸네일 랜덤
                List<Picture> pictures = tripPiece.getPictures();
                Random random = new Random();
                int randomIndex = random.nextInt(pictures.size());

                tripPieceListDto.setCategory(Category.PICTURE);
                tripPieceListDto.setMediaUrl(pictures.get(randomIndex).getPictureUrl());
            }
            else if (category == Category.VIDEO || category == Category.WHERE)
            {
                List<Video> videos = tripPiece.getVideos();
                Video video = videos.get(0);

                tripPieceListDto.setCategory(Category.VIDEO);
                tripPieceListDto.setMediaUrl(video.getVideoUrl());
            }

            tripPieceListDto.setCreatedAt(tripPiece.getCreatedAt());
            tripPieceListDto.setCountryName(country.getName());
            tripPieceListDto.setCityName(city.getName());

            tripPieceList.add(tripPieceListDto);
        }

        return tripPieceList;
    }

    @Transactional
    public List<TripPieceResponseDto.TripPieceListDto> getMemoList(String sort) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 정렬 기준에 따라 데이터를 조회
        List<TripPiece> tripPieces = "earliest".equalsIgnoreCase(sort)
                ? tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtAsc(userId, Category.MEMO, Category.EMOJI)
                : tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtDesc(userId, Category.MEMO, Category.EMOJI);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();

        for (TripPiece tripPiece : tripPieces) {
            Travel travel = tripPiece.getTravel();
            City city = travel.getCity();
            Country country = city.getCountry();
            Category category = tripPiece.getCategory();

            TripPieceResponseDto.TripPieceListDto tripPieceListDto = new TripPieceResponseDto.TripPieceListDto();

            tripPieceListDto.setCategory(Category.MEMO);

            if (category == Category.MEMO)
            {
                tripPieceListDto.setMemo(tripPiece.getDescription());
            }
            else if (category == Category.EMOJI)
            {
                List<Emoji> emojis = tripPiece.getEmojis();

                tripPieceListDto.setMemo(emojis.get(0).getEmoji() + emojis.get(1).getEmoji() + emojis.get(2).getEmoji() + emojis.get(3).getEmoji());
            }

            tripPieceListDto.setCreatedAt(tripPiece.getCreatedAt());
            tripPieceListDto.setCountryName(country.getName());
            tripPieceListDto.setCityName(city.getName());

            tripPieceList.add(tripPieceListDto);
        }

        return tripPieceList;
    }

    @Transactional
    public List<TripPieceResponseDto.TripPieceListDto> getPictureList(String sort) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 정렬 기준에 따라 데이터를 조회
        List<TripPiece> tripPieces = "earliest".equalsIgnoreCase(sort)
                ? tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtAsc(userId, Category.PICTURE, Category.SELFIE)
                : tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtDesc(userId, Category.PICTURE, Category.SELFIE);


        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();

        for (TripPiece tripPiece : tripPieces) {
            Travel travel = tripPiece.getTravel();
            City city = travel.getCity();
            Country country = city.getCountry();

            TripPieceResponseDto.TripPieceListDto tripPieceListDto = new TripPieceResponseDto.TripPieceListDto();

            // 여러개 사진이 있다면, 썸네일 랜덤
            List<Picture> pictures = tripPiece.getPictures();
            Random random = new Random();
            int randomIndex = random.nextInt(pictures.size());

            tripPieceListDto.setCategory(Category.PICTURE);
            tripPieceListDto.setMediaUrl(pictures.get(randomIndex).getPictureUrl());
            tripPieceListDto.setCreatedAt(tripPiece.getCreatedAt());
            tripPieceListDto.setCountryName(country.getName());
            tripPieceListDto.setCityName(city.getName());

            tripPieceList.add(tripPieceListDto);
        }

        return tripPieceList;
    }

    @Transactional
    public List<TripPieceResponseDto.TripPieceListDto> getVideoList(String sort) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 정렬 기준에 따라 데이터를 조회
        List<TripPiece> tripPieces = "earliest".equalsIgnoreCase(sort)
                ? tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtAsc(userId, Category.VIDEO, Category.WHERE)
                : tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtDesc(userId, Category.VIDEO, Category.WHERE);


        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();

        for (TripPiece tripPiece : tripPieces) {
            Travel travel = tripPiece.getTravel();
            City city = travel.getCity();
            Country country = city.getCountry();

            TripPieceResponseDto.TripPieceListDto tripPieceListDto = new TripPieceResponseDto.TripPieceListDto();

            List<Video> videos = tripPiece.getVideos();
            Video video = videos.get(0);

            tripPieceListDto.setCategory(Category.VIDEO);
            tripPieceListDto.setMediaUrl(video.getVideoUrl());
            tripPieceListDto.setCreatedAt(tripPiece.getCreatedAt());
            tripPieceListDto.setCountryName(country.getName());
            tripPieceListDto.setCityName(city.getName());

            tripPieceList.add(tripPieceListDto);
        }

        return tripPieceList;
    }

    @Transactional
    public TripPieceResponseDto.getTripPieceDto getTripPiece(Long tripPieceId) {
        TripPiece tripPiece = tripPieceRepository.getById(tripPieceId);
        Travel travel = tripPiece.getTravel();
        City city = travel.getCity();
        Country country = city.getCountry();

        String countryName = country.getName();
        String cityName = city.getName();

        List<String> mediaUrls = new ArrayList<>();
        String emojis = null;

        if (tripPiece.getCategory() == Category.PICTURE || tripPiece.getCategory() == Category.SELFIE)
        {
            List<Picture> pictures = tripPiece.getPictures();

            for(Picture picture : pictures)
            {
                mediaUrls.add(picture.getPictureUrl());
            }
        }
        else if (tripPiece.getCategory() == Category.VIDEO || tripPiece.getCategory() == Category.WHERE)
        {
            List<Video> videos = tripPiece.getVideos();

            mediaUrls.add(videos.get(0).getVideoUrl());
        }
        else if (tripPiece.getCategory() == Category.EMOJI)
        {
            List<Emoji> emojiList = tripPiece.getEmojis();

            emojis = emojiList.get(0).getEmoji() + emojiList.get(1).getEmoji() + emojiList.get(2).getEmoji() + emojiList.get(3).getEmoji();
        }

        return TripPieceConverter.toTripPiece(tripPiece, countryName, cityName, mediaUrls, emojis);

    }

    @Transactional
    public void delete(Long id) {
        // 이모지 삭제
        List<Emoji> emojis = emojiRepository.findByTripPieceId(id);
        emojiRepository.deleteAll(emojis);

        // 사진 삭제
        List<Picture> pictures = pictureRepository.findByTripPieceId(id);
        pictureRepository.deleteAll(pictures);

        // 영상 삭제
        List<Video> videos = videoRepository.findByTripPieceId(id);
        videoRepository.deleteAll(videos);

        // 여행 조각 삭제
        TripPiece tripPiece = tripPieceRepository.findById(id)
                        .orElseThrow(() -> new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE));

        tripPieceRepository.delete(tripPiece);
    }

    /* 여행 조각 수정 */
    @Transactional
    public Long memoUpdate(Long id, TripPieceRequestDto.update request) {
        TripPiece tripPiece = tripPieceRepository.findById(id)
                .orElseThrow(() -> new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE));

        if(tripPiece.getCategory() != Category.MEMO)
            throw new BadRequestHandler(ErrorStatus.INVALID_TRIPPIECE_CATEGORY);

        tripPiece.setDescription(request.getDescription());

        return id;
    }

    @Transactional
    public Long pictureUpdate(Long id, TripPieceRequestDto.update request, List<MultipartFile> files) {
        TripPiece tripPiece = tripPieceRepository.findById(id)
                .orElseThrow(() -> new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE));

        // 원래 여행조각의 타입이 PICTURE 또는 SELFIE 인지 검사
        if (tripPiece.getCategory() != Category.PICTURE
            && tripPiece.getCategory() != Category.SELFIE)
            throw new BadRequestHandler(ErrorStatus.INVALID_TRIPPIECE_CATEGORY);

        // 기존에 저장되어있던  사진들은 모두 삭제
        List<Picture> pictures = pictureRepository.findByTripPieceId(id);
        pictureRepository.deleteAll(pictures);

        // 사진 s3 업로드
        int pictureNum = files.size();

        List<Uuid> uuids = new ArrayList<>();

        for(int i = 0; i < pictureNum; i++) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
            uuids.add(savedUuid);
        }

        List<String> pictureUrls = s3Manager.saveFiles(s3Manager.generateTripPieceKeyNames(uuids), files);
        List<Picture> newPictures = new ArrayList<>();

        for(int i = 0; i < pictureNum; i++) {
            Picture newPicture = TripPieceConverter.toTripPiecePicture(pictureUrls.get(i), tripPiece);
            pictureRepository.save(newPicture);
            newPictures.add(newPicture);
        }

        // 업데이트
        tripPiece.setDescription(request.getDescription());
        tripPiece.setPictures(newPictures);

        return id;
    }

    @Transactional
    public Long videoUpdate(Long id, TripPieceRequestDto.update request, MultipartFile file) {
        TripPiece tripPiece = tripPieceRepository.findById(id)
                .orElseThrow(() -> new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE));

        // 원래 여행조각의 타입이 VIDEO 또는 WHERE 인지 검사
        if (tripPiece.getCategory() != Category.VIDEO
            && tripPiece.getCategory() != Category.WHERE)
            throw new BadRequestHandler(ErrorStatus.INVALID_TRIPPIECE_CATEGORY);

        // 기존에 저장되어있던 비디오들은 모두 삭제
        List<Video> videos = videoRepository.findByTripPieceId(id);
        videoRepository.deleteAll(videos);

        // 동영상 s3 업로드
        List<Video> newVideos = new ArrayList<>();

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
            .uuid(uuid).build());

        String videoUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), file);

        Video newVideo = TripPieceConverter.toTripPieceVideo(videoUrl, tripPiece);

        videoRepository.save(newVideo);
        newVideos.add(newVideo);

        tripPiece.setDescription(request.getDescription());
        tripPiece.setVideos(newVideos);

        return id;
    }

    @Transactional
    public Long emojiUpdate(Long id, TripPieceRequestDto.update request, List<String> emojis) {
        TripPiece tripPiece = tripPieceRepository.findById(id)
                .orElseThrow(() -> new NotFoundHandler(ErrorStatus.NOT_FOUND_TRIPPIECE));

        if (tripPiece.getCategory() != Category.EMOJI)
            throw new BadRequestHandler(ErrorStatus.INVALID_TRIPPIECE_CATEGORY);

        // 기존의 저장되어있던 이모지들은 삭제
        List<Emoji> existingEmojis = emojiRepository.findByTripPieceId(id);
        emojiRepository.deleteAll(existingEmojis);

        List<Emoji> newEmojis = new ArrayList<>();

        for(String emoji : emojis) {
            Emoji newEmoji = TripPieceConverter.toTripPieceEmoji(emoji, tripPiece);
            emojiRepository.save(newEmoji);
            newEmojis.add(newEmoji);
        }

        tripPiece.setDescription(request.getDescription());
        tripPiece.setEmojis(newEmojis);

        return id;
    }

}
