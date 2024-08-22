package umc.TripPiece.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.TripPiece.domain.enums.Category;
import umc.TripPiece.domain.enums.TravelStatus;
//import umc.TripPiece.domain.enums.Category;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TravelResponseDto {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private Long travelId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTripPieceResultDto {
        Long tripPieceId;
        LocalDate createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripSummaryDto {
        private String title;
        private String city;
        private String country;
        private String countryImage;
        private LocalDate startDate;
        private LocalDate endDate;
        private int totalPieces;
        private int memoCount;
        private int pictureCount;
        private int videoCount;
        private List<TripPieceSummaryDto> pictureSummaries;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripPieceSummaryDto {
        private Long id;
        private String description;
        private Category category;
        private List<String> mediaUrls;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySummaryDto {
        //private LocalDateTime date;
        private List<TripPieceSummaryDto> tripPieces;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelListDto {
        private Long id;
        private String title;
        private String thumbnail;
        @JsonFormat(pattern = "yyyy/MM/dd")
        private LocalDate startDate;
        @JsonFormat(pattern = "yyyy/MM/dd")
        private LocalDate endDate;
        private String cityName;
        private String countryName;
        private TravelStatus status;
        private String countryImage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getOngoingTravelResultDto {
        String profileImg;
        String nickname;
        String title;
        String cityName;
        String countryName;
        LocalDate startDate;
        LocalDate endDate;
        Long dayCount;
        Integer memoNum;
        Integer pictureNum;
        Integer videoNum;
    }

}
