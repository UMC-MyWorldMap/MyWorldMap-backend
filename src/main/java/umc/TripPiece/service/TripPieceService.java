package umc.TripPiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.TripPiece.domain.*;
import umc.TripPiece.domain.enums.Category;
import umc.TripPiece.repository.TripPieceRepository;
import umc.TripPiece.web.dto.response.TripPieceResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TripPieceService {

    private final TripPieceRepository tripPieceRepository;

    @Transactional
    public List<TripPieceResponseDto.TripPieceListDto> getTripPieceListLatest(Long userId) {
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
    public List<TripPieceResponseDto.TripPieceListDto> getTripPieceListEarliest(Long userId) {
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
}
