package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.TripPiece.converter.TripPieceConverter;
import umc.TripPiece.domain.*;
import umc.TripPiece.domain.enums.Category;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.TripPieceRepository;
import umc.TripPiece.repository.UserRepository;
import umc.TripPiece.web.dto.response.TripPieceResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TripPieceService {

    private final TripPieceRepository tripPieceRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public List<TripPieceResponseDto.TripPieceListDto> getTripPieceListLatest(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByUserIdOrderByCreatedAtDesc(userId);
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
    public List<TripPieceResponseDto.TripPieceListDto> getTripPieceListEarliest(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByUserIdOrderByCreatedAtAsc(userId);
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
    public List<TripPieceResponseDto.TripPieceListDto> getMemoListLatest(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtDesc(userId, Category.MEMO, Category.EMOJI);

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
    public List<TripPieceResponseDto.TripPieceListDto> getMemoListEarliest(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtAsc(userId, Category.MEMO, Category.EMOJI);

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
    public List<TripPieceResponseDto.TripPieceListDto> getPictureListLatest(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtDesc(userId, Category.PICTURE, Category.SELFIE);

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
    public List<TripPieceResponseDto.TripPieceListDto> getPictureListEarliest(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtAsc(userId, Category.PICTURE, Category.SELFIE);

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
    public List<TripPieceResponseDto.TripPieceListDto> getVideoListLatest(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtDesc(userId, Category.VIDEO, Category.WHERE);

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
    public List<TripPieceResponseDto.TripPieceListDto> getVideoListEarliest(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByUserIdAndCategoryOrCategoryOrderByCreatedAtAsc(userId, Category.VIDEO, Category.WHERE);

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


}
