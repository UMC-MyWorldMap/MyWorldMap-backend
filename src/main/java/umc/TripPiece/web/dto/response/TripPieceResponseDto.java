package umc.TripPiece.web.dto.response;

import lombok.*;
import umc.TripPiece.domain.enums.Category;

import java.time.LocalDateTime;
import java.util.List;

public class TripPieceResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getTripPieceDto {
        LocalDateTime createdAt;
        String countryName;
        String cityName;
        Category category;
        List<String> mediaUrls;
        String emojis;
        String description;

    }
}
