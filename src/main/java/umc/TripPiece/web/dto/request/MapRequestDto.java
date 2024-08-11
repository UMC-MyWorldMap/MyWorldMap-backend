package umc.TripPiece.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import umc.TripPiece.domain.enums.Color;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapRequestDto {
    private Long userId;
    private String countryCode;
    private Color color;

    public void setColor(String colorCode) {
        this.color = Color.fromString(colorCode);
    }
}