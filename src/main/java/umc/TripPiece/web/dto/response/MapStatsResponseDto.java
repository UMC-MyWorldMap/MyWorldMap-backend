package umc.TripPiece.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapStatsResponseDto {
    private long countryCount;
    private long cityCount;
}
