package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        Long user_id = jwtUtil.getuser_idFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByuser_idOrderByCreatedAtDesc(user_id);
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
        Long user_id = jwtUtil.getuser_idFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByuser_idOrderByCreatedAtAsc(user_id);
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
        Long user_id = jwtUtil.getuser_idFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByuser_idAndCategoryOrCategoryOrderByCreatedAtDesc(user_id, Category.MEMO, Category.EMOJI);

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
        Long user_id = jwtUtil.getuser_idFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByuser_idAndCategoryOrCategoryOrderByCreatedAtAsc(user_id, Category.MEMO, Category.EMOJI);

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
        Long user_id = jwtUtil.getuser_idFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByuser_idAndCategoryOrCategoryOrderByCreatedAtDesc(user_id, Category.PICTURE, Category.SELFIE);

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
        Long user_id = jwtUtil.getuser_idFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByuser_idAndCategoryOrCategoryOrderByCreatedAtAsc(user_id, Category.PICTURE, Category.SELFIE);

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
        Long user_id = jwtUtil.getuser_idFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByuser_idAndCategoryOrCategoryOrderByCreatedAtDesc(user_id, Category.VIDEO, Category.WHERE);

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
        Long user_id = jwtUtil.getuser_idFromToken(token);

        List<TripPieceResponseDto.TripPieceListDto> tripPieceList = new ArrayList<>();
        List<TripPiece> tripPieces = tripPieceRepository.findByuser_idAndCategoryOrCategoryOrderByCreatedAtAsc(user_id, Category.VIDEO, Category.WHERE);

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



}
