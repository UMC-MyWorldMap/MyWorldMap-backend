package umc.TripPiece.converter;

import umc.TripPiece.domain.*;
import umc.TripPiece.domain.enums.Category;
import umc.TripPiece.web.dto.request.TravelRequestDto;
import umc.TripPiece.web.dto.response.TravelResponseDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TravelConverter {

    public static TripPiece toTripPieceMemo(TravelRequestDto.MemoDto request, User user) {
        return TripPiece.builder()
                .description(request.getDescription())
                .user(user)
                .build();
    }

    public static TravelResponseDto.CreateTripPieceResultDto toCreateTripPieceResultDto(TripPiece tripPiece) {
        return TravelResponseDto.CreateTripPieceResultDto.builder()
                .tripPieceId(tripPiece.getId())
                .createdAt(LocalDate.now())
                .build();
    }

    public static Travel toTravel(TravelRequestDto.Create request, City city){
        return Travel.builder().city(city).title(request.getTitle()).startDate(request.getStartDate().atStartOfDay()).endDate(request.getEndDate().atStartOfDay()).build();
    }
    public static TravelResponseDto.Create toCreateResponseDto(Travel travel){
        return new TravelResponseDto.Create(travel.getId());
    }
    public static TravelResponseDto.TripSummaryDto toTripSummary(Travel travel, List<TripPiece> tripPieces) {
        TravelResponseDto.TripSummaryDto.TripSummaryDtoBuilder summaryBuilder = TravelResponseDto.TripSummaryDto.builder()
                .title(travel.getTitle())
                .city(travel.getCity().getName())
                .country(travel.getCity().getCountry().getName())
                .countryImage(travel.getCity().getCountry().getCountryImage())
                .startDate(travel.getStartDate().toLocalDate())
                .endDate(travel.getEndDate().toLocalDate())
                .totalPieces(tripPieces.size())
                .memoCount((int) tripPieces.stream().filter(tp -> tp.getCategory().equals(Category.MEMO) || tp.getCategory().equals(Category.EMOJI)).count())
                .pictureCount((int) tripPieces.stream().filter(tp -> tp.getCategory().equals(Category.PICTURE) || tp.getCategory().equals(Category.SELFIE)).count())
                .videoCount((int) tripPieces.stream().filter(tp -> tp.getCategory().equals(Category.VIDEO) || tp.getCategory().equals(Category.WHERE)).count());

        List<TravelResponseDto.TripPieceSummaryDto> pictureSummaries = tripPieces.stream()
                .filter(tp -> tp.getCategory().equals(Category.PICTURE) || tp.getCategory().equals(Category.SELFIE))
                .limit(9)
                .map(tp -> TravelResponseDto.TripPieceSummaryDto.builder()
                        .id(tp.getId())
                        .description(tp.getDescription())
                        .category(tp.getCategory())
                        .mediaUrls(tp.getPictures().stream().map(Picture::getPictureUrl).collect(Collectors.toList()))
                        .createdAt(tp.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        summaryBuilder.pictureSummaries(pictureSummaries);

        return summaryBuilder.build();
    }

    public static TravelResponseDto.TripPieceSummaryDto toTripPieceSummary(TripPiece tripPiece){
        List<String> mediaUrls = null;
        if (tripPiece.getCategory() == Category.PICTURE || tripPiece.getCategory() == Category.SELFIE) {
            mediaUrls = tripPiece.getPictures().stream()
                    .map(picture -> picture.getPictureUrl())
                    .collect(Collectors.toList());
        } else if (tripPiece.getCategory() == Category.VIDEO) {
            mediaUrls = tripPiece.getVideos().stream()
                    .map(video -> video.getVideoUrl())
                    .collect(Collectors.toList());
            
        }

        return TravelResponseDto.TripPieceSummaryDto.builder()
                .id(tripPiece.getId())
                .description(tripPiece.getDescription())
                .category(tripPiece.getCategory())
                .mediaUrls(mediaUrls)
                .createdAt(tripPiece.getCreatedAt())
                .build();

    }

    public static TravelResponseDto.TravelListDto toTravelListDto(Travel travel) {
        return TravelResponseDto.TravelListDto.builder()
                .id(travel.getId())
                .title(travel.getTitle())
                .thumbnail(travel.getThumbnail())
                .startDate(travel.getStartDate().toLocalDate())
                .endDate(travel.getEndDate().toLocalDate())
                .cityName(travel.getCity().getName())
                .countryName(travel.getCity().getCountry().getName())
                .status(travel.getStatus())
                .build();
    }

    public static TravelResponseDto.getOngoingTravelResultDto toOngoingTravelResultDto(Travel travel) {
        return TravelResponseDto.getOngoingTravelResultDto.builder()
                .memoNum(travel.getMemoNum())
                .pictureNum(travel.getPictureNum())
                .videoNum(travel.getVideoNum())
                .build();
    }


}
