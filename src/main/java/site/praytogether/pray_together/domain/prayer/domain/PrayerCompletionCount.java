package site.praytogether.pray_together.domain.prayer.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Value;
import site.praytogether.pray_together.domain.prayer.infrastructure.persistence.PrayerCompletionWithMemberEntity;

@Value
@Getter
public class PrayerCompletionCount {
  Long titleId;
  Long memberId;
  String memberName;
  Long prayerCount;

  /**
   * PrayerCompletionWithMemberEntity 리스트를 PrayerCompletionMember 리스트로 변환
   * 같은 회원이 여러 번 기도한 경우 prayerId별로 그룹핑하여 카운트
   *
   * @param entities PrayerCompletionWithMemberEntity 리스트
   * @return PrayerCompletionMember 리스트
   */
  public static List<PrayerCompletionCount> fromEntities(
      List<PrayerCompletionWithMemberEntity> entities) {

    // prayerId별로 그룹핑하여 카운트 (같은 사람이 여러 번 기도한 경우)
    Map<Long, List<PrayerCompletionWithMemberEntity>> completionsByPrayerId =
        entities.stream().collect(Collectors.groupingBy(PrayerCompletionWithMemberEntity::getPrayerId));

    // 각 prayerId에 대해 PrayerCompletionMember 생성
    return completionsByPrayerId.entrySet().stream()
        .map(
            entry -> {
              Long prayerId = entry.getKey();
              List<PrayerCompletionWithMemberEntity> memberCompletions = entry.getValue();
              long count = memberCompletions.size();

              // 첫 번째 항목에서 정보 추출 (같은 prayerId라면 동일한 정보)
              PrayerCompletionWithMemberEntity first = memberCompletions.get(0);
              Long titleId = first.getTitleId();
              String memberName = first.getPrayerName();

              return new PrayerCompletionCount(titleId, prayerId, memberName, count);
            })
        .toList();
  }

  /**
   * Member가 탈퇴하거나 삭제된 경우 true 반환
   */
  public boolean hasDeletedMember() {
    return memberName == null;
  }

  /**
   * 표시용 회원 이름 반환 (삭제된 경우 "알 수 없음")
   */
  public String getDisplayName() {
    return memberName != null ? memberName : "알 수 없음";
  }
}
