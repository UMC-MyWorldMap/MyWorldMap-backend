package umc.TripPiece.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TravelRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String cityName;
        private String countryName;
        @Size(max = 15, message = "제목은 15자 이내")
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    public static class MemoDto {

        @NotBlank
        String description;
    }

}
