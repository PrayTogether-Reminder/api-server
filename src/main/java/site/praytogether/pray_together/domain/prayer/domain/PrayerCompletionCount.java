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
   * 같은 회원이 같은 기도 제목에 여러 번 기도한 경우 (titleId, prayerId) 조합으로 그룹핑하여 카운트
   *
   * @param entities PrayerCompletionWithMemberEntity 리스트
   * @return PrayerCompletionMember 리스트
   */
  public static List<PrayerCompletionCount> fromEntities(
      List<PrayerCompletionWithMemberEntity> entities) {

    // (titleId, prayerId) 조합별로 그룹핑하여 카운트
    Map<PrayerPairKey, List<PrayerCompletionWithMemberEntity>> completionsByTitleAndPrayer =
        entities.stream()
            .collect(
                Collectors.groupingBy(
                    entity -> new PrayerPairKey(entity.getTitleId(), entity.getPrayerId())));

    // 각 (titleId, prayerId) 조합에 대해 PrayerCompletionCount 생성
    return completionsByTitleAndPrayer.entrySet().stream()
        .map(
            entry -> {
              List<PrayerCompletionWithMemberEntity> memberCompletions = entry.getValue();
              long count = memberCompletions.size();

              // 첫 번째 항목에서 정보 추출 (같은 조합이라면 동일한 정보)
              PrayerCompletionWithMemberEntity first = memberCompletions.get(0);
              Long titleId = first.getTitleId();
              Long prayerId = first.getPrayerId();
              String memberName = first.getPrayerName();

              return new PrayerCompletionCount(titleId, prayerId, memberName, count);
            })
        .toList();
  }

  /**
   * 기도 제목과 기도자의 조합을 나타내는 복합 키
   * Map의 키로 사용하기 위해 equals/hashCode가 자동 구현됨
   */
  private record PrayerPairKey(Long titleId, Long prayerId) {}

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
