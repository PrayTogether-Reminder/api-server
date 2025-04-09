package site.praytogether.pray_together.domain.prayer.service;

import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.DEFAULT_INFINITE_SCROLL_AFTER;
import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.PRAYER_TITLES_INFINITE_SCROLL_SIZE;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.prayer.exception.PrayerTitleNotFoundException;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitleInfo;
import site.praytogether.pray_together.domain.prayer.respository.PrayerTitleRepository;
import site.praytogether.pray_together.domain.room.model.Room;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrayerTitleService {
  private final PrayerTitleRepository titleRepository;

  public PrayerTitle fetchById(Long titleId) {
    return titleRepository
        .findById(titleId)
        .orElseThrow(() -> new PrayerTitleNotFoundException(titleId));
  }

  public List<PrayerTitleInfo> fetchTitlesByRoom(Long roomId, String after) {
    if (DEFAULT_INFINITE_SCROLL_AFTER.equals(after)) {
      return titleRepository.findFirstPrayerTitleInfosOrderByCreatedTimeDesc(
          roomId, PageRequest.of(0, PRAYER_TITLES_INFINITE_SCROLL_SIZE));
    }
    return titleRepository.findPrayerTitleInfosOrderByCreatedTimeDesc(
        roomId, Instant.parse(after), PageRequest.of(0, PRAYER_TITLES_INFINITE_SCROLL_SIZE));
  }

  @Transactional
  public PrayerTitle create(Room roomRef, String title) {
    PrayerTitle prayerTitle = PrayerTitle.create(roomRef, title);
    return titleRepository.save(prayerTitle);
  }

  @Transactional
  public void update(PrayerTitle prayerTitle, String newTitle) {
    prayerTitle.updateTitle(newTitle);
  }
}
