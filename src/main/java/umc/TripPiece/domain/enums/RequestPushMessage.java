package umc.TripPiece.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestPushMessage {

    WEEKLY_TRAVEL("여행에 다녀오셨나요? ", "당신의 여행 조각들을 기록해주세요!");

    String title;
    String body;
}
