package site.praytogether.pray_together.domain.prayer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.respository.PrayerTitleRepository;
import site.praytogether.pray_together.domain.room.model.Room;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrayerTitleService {
  private final PrayerTitleRepository titleRepository;

  @Transactional
  public PrayerTitle create(Room roomRef, String title) {
    PrayerTitle prayerTitle = PrayerTitle.create(roomRef, title);
    return titleRepository.save(prayerTitle);
  }
}
