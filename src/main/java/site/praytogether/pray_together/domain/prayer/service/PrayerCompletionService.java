package site.praytogether.pray_together.domain.prayer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.prayer.model.PrayerCompletion;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.respository.PrayerCompletionRepository;

@Service
@RequiredArgsConstructor
public class PrayerCompletionService {
  private final PrayerCompletionRepository completionRepository;

  public void create(Long prayerId, PrayerTitle prayerTitle) {
    PrayerCompletion prayerCompletion = PrayerCompletion.create(prayerId, prayerTitle);
    completionRepository.save(prayerCompletion);
  }
}
