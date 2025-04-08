package site.praytogether.pray_together.domain.prayer.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member_room.service.MemberRoomService;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleScrollRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleScrollResponse;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitleInfo;
import site.praytogether.pray_together.domain.prayer.service.PrayerContentService;
import site.praytogether.pray_together.domain.prayer.service.PrayerTitleService;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.service.RoomService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrayerApplicationService {
  private final PrayerTitleService titleService;
  private final PrayerContentService contentService;
  private final RoomService roomService;
  private final MemberRoomService memberRoomService;

  public PrayerContentResponse fetchPrayerContent(Long memberId, Long titleId) {
    PrayerTitle prayerTitle = titleService.fetchById(titleId);
    Room room = prayerTitle.getRoom();
    memberRoomService.validateMemberExistInRoom(memberId, room.getId());

    List<PrayerContentInfo> prayerContentInfos = contentService.fetchContents(titleId);
    return PrayerContentResponse.from(prayerContentInfos);
  }

  public PrayerTitleScrollResponse fetchPrayerTitleInfiniteScroll(
      Long memberId, PrayerTitleScrollRequest request) {
    memberRoomService.validateMemberExistInRoom(memberId, request.getRoomId());
    List<PrayerTitleInfo> titleInfos =
        titleService.fetchTitlesByRoom(request.getRoomId(), request.getAfter());
    return PrayerTitleScrollResponse.from(titleInfos);
  }

  @Transactional
  public MessageResponse createPrayers(Long memberId, PrayerCreateRequest request) {
    Room roomRef = roomService.getRefOrThrow(request.getRoomId());
    memberRoomService.validateMemberExistInRoom(memberId, roomRef.getId());
    PrayerTitle title = titleService.create(roomRef, request.getTitle());
    contentService.save(title, request.getContents());
    return MessageResponse.of("기도 제목을 생성했습니다.");
  }
}
