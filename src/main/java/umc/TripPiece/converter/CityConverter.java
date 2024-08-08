package umc.TripPiece.converter;

import umc.TripPiece.domain.City;
import umc.TripPiece.web.dto.response.CityResponseDto;

public class CityConverter {
    public static CityResponseDto.searchDto toSearchDto(City city){
        return new CityResponseDto.searchDto(
                city.getName(),
                city.getCountry().getName(),
                city.getComment(),
                city.getCountry().getCountryImage(),
                city.getLogCount()
        );
    }
}
