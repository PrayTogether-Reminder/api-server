package site.praytogether.pray_together.domain.prayer.infrastructure.persistence;

import lombok.Value;

/**
 * PrayerCompletion과 Member 정보를 함께 조회하기 위한 DTO
 * Repository 레이어에서 LEFT JOIN 결과를 담는 용도
 * 순수한 데이터 홀더로 비즈니스 로직은 도메인 객체(PrayerCompletionMember)에 위임
 */
@Value
public class PrayerCompletionWithMemberEntity {
  Long completionId;
  Long titleId;
  Long prayerId;
  String prayerName;
}
