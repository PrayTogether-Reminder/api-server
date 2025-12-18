package site.praytogether.pray_together.domain.prayer.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayerCompletionService {
  private final PrayerCompletionRepository completionRepository;

  public void create(Long prayerId, PrayerTitle prayerTitle) {
    PrayerCompletion prayerCompletion = PrayerCompletion.create(prayerId, prayerTitle);
    completionRepository.save(prayerCompletion);
  }
}
