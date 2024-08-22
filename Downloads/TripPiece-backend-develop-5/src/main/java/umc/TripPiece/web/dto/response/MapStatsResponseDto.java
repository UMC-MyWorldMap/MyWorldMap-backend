package umc.TripPiece.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapStatsResponseDto {

    private long countryCount;
    private long cityCount;
    private List<String> countryCodes;
    private List<Long> cityIds;
    private String profileImg;   // 프로필 이미지
    private String nickname;     // 닉네임

}