package site.praytogether.pray_together.domain.prayer.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.prayer.infrastructure.persistence.PrayerCompletionWithMemberEntity;

@Service
@RequiredArgsConstructor
public class PrayerCompletionService {
  private final PrayerCompletionRepository completionRepository;

  public void create(Long prayerId, PrayerTitle prayerTitle) {
    PrayerCompletion prayerCompletion = PrayerCompletion.create(prayerId, prayerTitle);
    completionRepository.save(prayerCompletion);
  }

  /**
   * 주어진 PrayerTitle ID 목록에 해당하는 PrayerCompletion과 Member 정보를 조회
   *
   * @param titleIds PrayerTitle ID 목록
   * @return PrayerCompletion과 Member 정보를 담은 DTO 리스트
   */
  public List<PrayerCompletionCount> fetchCountByTitleIds(List<Long> titleIds) {
    List<PrayerCompletionWithMemberEntity> entities = completionRepository.findWithMemberByTitleIds(titleIds);
    return PrayerCompletionCount.fromEntities(entities);
  }
}
