package site.praytogether.pray_together.domain.prayer.application;

import static site.praytogether.pray_together.domain.notification.constant.NotificationMessageFormat.PrayerCompletion;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.fcm_token.service.FcmTokenService;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.domain.member_room.service.MemberRoomService;
import site.praytogether.pray_together.domain.notification.gateway.NotificationGateway;
import site.praytogether.pray_together.domain.notification.service.PrayerCompletionNotificationService;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCompletionCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleInfiniteScrollRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleInfiniteScrollResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleUpdateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentUpdateRequest;
import site.praytogether.pray_together.domain.prayer.exception.PrayerContentNotFoundException;
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
  private final FcmTokenService fcmTokenService;
  private final NotificationGateway notificationGateway;


  public PrayerTitleInfiniteScrollResponse fetchPrayerTitleInfiniteScroll(
      Long memberId, PrayerTitleInfiniteScrollRequest request) {
    memberRoomService.validateMemberExistInRoom(memberId, request.getRoomId());
    List<PrayerTitleInfo> titleInfos =
        titleService.fetchTitlesByRoom(request.getRoomId(), request.getAfter());
    return PrayerTitleInfiniteScrollResponse.from(titleInfos);
  }


  @Transactional
  public PrayerTitleResponse createPrayerTitle(Long memberId, PrayerTitleCreateRequest request) {
    Room roomRef = roomService.getRefOrThrow(request.getRoomId());
    memberRoomService.validateMemberExistInRoom(memberId, roomRef.getId());
    
    PrayerTitle title = titleService.create(roomRef, request.getTitle());
    return PrayerTitleResponse.of(
        title.getId(), 
        title.getTitle(), 
        title.getCreatedTime()
    );
  }

  @Transactional
  public MessageResponse updatePrayerTitle(Long memberId, Long titleId, PrayerTitleUpdateRequest request) {
    validateMemberExistInRoomByTitleId(memberId, titleId);
    PrayerTitle prayerTitle = titleService.fetchById(titleId);
    titleService.update(prayerTitle, request.getChangedTitle());
    return MessageResponse.of("기도 제목을 변경했습니다.");
  }

  @Transactional
  public MessageResponse deletePrayerTitle(Long memberId, Long titleId) {
    validateMemberExistInRoomByTitleId(memberId, titleId);
    titleService.delete(titleId);
    return MessageResponse.of("기도 제목을 삭제했습니다.");
  }

  @Transactional
  public MessageResponse createPrayerContent(Long writerId, Long titleId, PrayerContentCreateRequest request) {
    validateMemberExistInRoomByTitleId(writerId, titleId);
    PrayerTitle title = titleService.fetchById(titleId);
    Member writer = memberService.fetchById(writerId);
    contentService.save(title, request,writer);
    return MessageResponse.of("기도 내용을 생성했습니다.");
  }

  @Transactional
  public MessageResponse updatePrayerContent(Long memberId, Long titleId, Long contentId, PrayerContentUpdateRequest request) {
    validateMemberExistInRoomByTitleId(memberId, titleId);
    if (!contentService.existsByIdAndTitleId(contentId, titleId)) {
      throw new PrayerContentNotFoundException(contentId, titleId);
    }
    contentService.update(contentId, request.getChangedContent());
    return MessageResponse.of("기도 내용을 변경했습니다.");
  }

  public PrayerContentResponse fetchPrayerContents(Long memberId, Long titleId) {
    validateMemberExistInRoomByTitleId(memberId, titleId);
    List<PrayerContentInfo> prayerContentInfos = contentService.fetchContents(titleId);
    return PrayerContentResponse.from(prayerContentInfos);
  }

  @Transactional
  public MessageResponse deletePrayerContent(Long memberId, Long titleId, Long contentId) {
    validateMemberExistInRoomByTitleId(memberId, titleId);
    contentService.deleteById(titleId, contentId);
    return MessageResponse.of("기도 내용을 삭제했습니다.");
  }

  @Transactional
  public MessageResponse completePrayerAndNotify(
      Long senderId, Long prayerTitleId, PrayerCompletionCreateRequest request) {
    validateMemberExistInRoomByTitleId(senderId, prayerTitleId);
    PrayerTitle prayerTitle = titleService.fetchById(prayerTitleId);
    completionService.create(senderId, prayerTitle);

    List<Long> memberIds = memberRoomService.fetchMemberIdsInRoom(request.getRoomId());
    Member sender = memberService.fetchById(senderId);
    String message = String.format(PrayerCompletion, sender.getName(), prayerTitle.getTitle());
    notificationService.create(senderId, memberIds, message, prayerTitle);
    List<FcmToken> fcmTokens = fcmTokenService.fetchTokensByMemberIds(memberIds);
    notificationGateway.notifyCompletePrayer(fcmTokens, message, fcmTokenService::deleteByToken);
    return MessageResponse.of("기도 완료 알림을 전송했습니다.");
  }

  private void validateMemberExistInRoomByTitleId(Long memberId, Long titleId) {
    PrayerTitle prayerTitle = titleService.fetchById(titleId);
    Room room = prayerTitle.getRoom();
    memberRoomService.validateMemberExistInRoom(memberId, room.getId());
  }
}
