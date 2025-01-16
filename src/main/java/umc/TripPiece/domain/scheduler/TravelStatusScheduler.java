package umc.TripPiece.domain.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import umc.TripPiece.repository.TravelRepository;

@Component
@RequiredArgsConstructor
public class TravelStatusScheduler {
    private final TravelRepository travelRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 여행기의 기간을 검사하여 상태 변경
    public void updateExpiredStatuses() {
        travelRepository.updateCompletedStatuses();
    }
}
