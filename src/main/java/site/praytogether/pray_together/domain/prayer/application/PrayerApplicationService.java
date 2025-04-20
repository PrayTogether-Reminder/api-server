package site.praytogether.pray_together.domain.prayer.application;

import static site.praytogether.pray_together.domain.notification.constant.NotificationMessageFormat.PrayerCompletion;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.domain.member_room.service.MemberRoomService;
import site.praytogether.pray_together.domain.notification.event.EventPublisher;
import site.praytogether.pray_together.domain.notification.service.PrayerCompletionNotificationService;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCompletionCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleInfiniteScrollRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleInfiniteScrollResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerUpdateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitleInfo;
import site.praytogether.pray_together.domain.prayer.service.PrayerCompletionService;
import site.praytogether.pray_together.domain.prayer.service.PrayerContentService;
import site.praytogether.pray_together.domain.prayer.service.PrayerTitleService;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.service.RoomService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrayerApplicationService {
  // todo: 개별 service 마다의 @Transaction 삭제
  private final PrayerTitleService titleService;
  private final PrayerContentService contentService;
  private final RoomService roomService;
  private final MemberRoomService memberRoomService;
  private final MemberService memberService;
  private final PrayerCompletionService completionService;
  private final PrayerCompletionNotificationService notificationService;
  private final EventPublisher eventPublisher;

  public PrayerContentResponse fetchPrayerContent(Long memberId, Long titleId) {
    validateMemberExistInRoomByTitleId(memberId, titleId);
    List<PrayerContentInfo> prayerContentInfos = contentService.fetchContents(titleId);
    return PrayerContentResponse.from(prayerContentInfos);
  }

  public PrayerTitleInfiniteScrollResponse fetchPrayerTitleInfiniteScroll(
      Long memberId, PrayerTitleInfiniteScrollRequest request) {
    memberRoomService.validateMemberExistInRoom(memberId, request.getRoomId());
    List<PrayerTitleInfo> titleInfos =
        titleService.fetchTitlesByRoom(request.getRoomId(), request.getAfter());
    return PrayerTitleInfiniteScrollResponse.from(titleInfos);
  }

  @Transactional
  public MessageResponse createPrayers(Long memberId, PrayerCreateRequest request) {
    Room roomRef = roomService.getRefOrThrow(request.getRoomId());
    memberRoomService.validateMemberExistInRoom(memberId, roomRef.getId());
    PrayerTitle title = titleService.create(roomRef, request.getTitle());
    contentService.save(title, request.getContents());
    return MessageResponse.of("기도 제목을 생성했습니다.");
  }

  @Transactional
  public MessageResponse updatePrayers(Long memberId, Long titleId, PrayerUpdateRequest request) {
    validateMemberExistInRoomByTitleId(memberId, titleId);
    PrayerTitle prayerTitle = titleService.fetchByIdWithContents(titleId);
    titleService.update(prayerTitle, request.getTitle());
    contentService.update(prayerTitle, request.getContents());
    return MessageResponse.of("기도를 변경했습니다.");
  }

  @Transactional
  public MessageResponse deletePrayer(Long memberId, Long titleId) {
    validateMemberExistInRoomByTitleId(memberId, titleId);
    titleService.delete(titleId);
    return MessageResponse.of("기도를 삭제했습니다.");
  }

  @Transactional
  public MessageResponse completePrayer(
      Long senderId, Long prayerTitleId, PrayerCompletionCreateRequest request) {
    validateMemberExistInRoomByTitleId(senderId, prayerTitleId);
    PrayerTitle prayerTitle = titleService.fetchById(prayerTitleId);
    completionService.create(senderId, prayerTitle);

    List<Long> memberIds = memberRoomService.fetchMemberIdsInRoom(request.getRoomId());
    Member member = memberService.fetchById(senderId);
    String message = String.format(PrayerCompletion, member.getName(), prayerTitle.getTitle());
    notificationService.create(senderId, memberIds, message, prayerTitle);

    eventPublisher.publishPrayerComplete();
    return MessageResponse.of("기도 완료 알림을 전송했습니다.");
  }

  private void validateMemberExistInRoomByTitleId(Long memberId, Long titleId) {
    PrayerTitle prayerTitle = titleService.fetchById(titleId);
    Room room = prayerTitle.getRoom();
    memberRoomService.validateMemberExistInRoom(memberId, room.getId());
  }
}
