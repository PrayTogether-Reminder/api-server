package site.praytogether.pray_together.domain.prayer.application.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import site.praytogether.pray_together.domain.prayer.domain.PrayerCompletionCount;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.presentation.dto.PrayerCompletionCountDto;
import site.praytogether.pray_together.domain.prayer.presentation.dto.PrayerTitleInfoDto;
import site.praytogether.pray_together.domain.prayer.presentation.dto.response.PrayerTitleInfiniteScrollResponse;

public final class PrayerTitleMapper {

  private PrayerTitleMapper() {
    // Utility class
  }

  /**
   * 빈 응답 생성
   */
  public static PrayerTitleInfiniteScrollResponse emptyResponse() {
    return new PrayerTitleInfiniteScrollResponse(Collections.emptyList());
  }

  /**
   * PrayerTitle 리스트와 PrayerCompletionCount 리스트를 응답 DTO로 변환
   *
   * @param titles PrayerTitle 리스트
   * @param counts 회원별 기도 완료 횟수 집계 정보 리스트
   * @return 무한 스크롤 응답
   */
  public static PrayerTitleInfiniteScrollResponse toResponse(
      List<PrayerTitle> titles, List<PrayerCompletionCount> counts) {

    // titleId별로 PrayerCompletionCount 그룹핑
    Map<Long, List<PrayerCompletionCount>> countsByTitle =
        counts.stream().collect(Collectors.groupingBy(PrayerCompletionCount::getTitleId));

    // 각 PrayerTitle을 PrayerTitleInfoDto로 변환
    List<PrayerTitleInfoDto> titleInfoDtos =
        titles.stream()
            .map(
                title -> {
                  List<PrayerCompletionCount> titleCounts =
                      countsByTitle.getOrDefault(title.getId(), Collections.emptyList());
                  return toTitleInfoDto(title, titleCounts);
                })
            .toList();

    return new PrayerTitleInfiniteScrollResponse(titleInfoDtos);
  }

  /**
   * PrayerTitle과 해당 Title의 PrayerCompletionCount 리스트를 PrayerTitleInfoDto로 변환
   *
   * @param title PrayerTitle
   * @param counts 해당 Title의 회원별 기도 완료 횟수 리스트 (이미 집계됨)
   * @return PrayerTitleInfoDto
   */
  private static PrayerTitleInfoDto toTitleInfoDto(
      PrayerTitle title, List<PrayerCompletionCount> counts) {

    // 이미 집계된 도메인 객체를 DTO로 변환
    List<PrayerCompletionCountDto> prayerDtos =
        counts.stream()
            .map(
                count ->
                    PrayerCompletionCountDto.builder()
                        .memberId(count.getMemberId())
                        .memberName(count.getDisplayName())
                        .prayerCount(count.getPrayerCount())
                        .build())
            .toList();

    return new PrayerTitleInfoDto(title.getId(), title.getTitle(), title.getCreatedTime(), prayerDtos);
  }
}
