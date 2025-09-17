package site.praytogether.pray_together.domain.prayer.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.exception.PrayerContentNotFoundException;
import site.praytogether.pray_together.domain.prayer.exception.PrayerContentDuplicateMemberException;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
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
    // 중복 체크: 이미 해당 기도 제목에 같은 이름으로 기도 내용을 작성했는지 확인
    if (contentRepository.existsByPrayerTitleIdAndMemberName(title.getId(), content.getMemberName())) {
      throw new PrayerContentDuplicateMemberException(title.getId(), content.getMemberName());
    }
    
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

  public PrayerContent fetchById(Long contentId) {
    return contentRepository.findById(contentId).orElseThrow(() -> new PrayerContentNotFoundException(contentId));
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
