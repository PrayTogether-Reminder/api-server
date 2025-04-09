package site.praytogether.pray_together.domain.prayer.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.model.PrayerRequestContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.model.PrayerUpdateContent;
import site.praytogether.pray_together.domain.prayer.respository.PrayerContentRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrayerContentService {
  private final PrayerContentRepository contentRepository;

  public List<PrayerContentInfo> fetchContents(Long titleId) {
    return contentRepository.findPrayerContentsByTitleId(titleId);
  }

  @Transactional
  public void save(PrayerTitle title, List<PrayerRequestContent> contents) {
    contents.forEach(
        content -> {
          PrayerContent newContent = PrayerContent.create(title, content);
          title.addContent(newContent);
        });
  }

  @Transactional
  public void update(PrayerTitle title, List<PrayerUpdateContent> contents) {
    List<PrayerContent> prayerContents = title.getPrayerContents();
    prayerContents.clear();
    contents.forEach(
        content -> {
          PrayerContent update = PrayerContent.update(title, content);
          prayerContents.add(update);
        });
  }
}
