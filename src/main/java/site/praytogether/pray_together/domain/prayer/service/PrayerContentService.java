package site.praytogether.pray_together.domain.prayer.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentUpdateRequest;
import site.praytogether.pray_together.domain.prayer.exception.PrayerContentNotFoundException;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.model.PrayerRequestContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
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

  public boolean existsByIdAndTitleId(Long contentId, Long titleId) {
    return contentRepository.existsByIdAndPrayerTitleId(contentId, titleId);
  }

  @Transactional
  public PrayerContent save(PrayerTitle title, PrayerContentCreateRequest content) {
    PrayerContent newContent = PrayerContent.create(title, content);
    title.addContent(newContent);
    return contentRepository.save(newContent);
  }

  @Transactional
  public PrayerContent update(Long contentId, String content) {
    PrayerContent prayerContent = contentRepository.findById(contentId)
        .orElseThrow(() -> new PrayerContentNotFoundException(contentId));
    prayerContent.updateContent(content);
    return prayerContent;
  }

  @Transactional
  public void deleteById(Long titleId, Long contentId) {
    if (!contentRepository.existsByIdAndPrayerTitleId(contentId, titleId)) {
      throw new PrayerContentNotFoundException(contentId, titleId);
    }
    contentRepository.deleteById(contentId);
  }

  @Transactional
  public void deleteAll(PrayerTitle title) {
    List<PrayerContent> prayerContents = title.getPrayerContents();
    prayerContents.clear();
  }
}
