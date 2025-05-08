package site.praytogether.pray_together.domain.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.domain.notification.dto.FcmTokenRegisterRequest;
import site.praytogether.pray_together.domain.notification.model.FcmToken;
import site.praytogether.pray_together.domain.notification.service.FcmTokenService;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenApplicationService {

  private final FcmTokenService fcmTokenService;
  private final MemberService memberService;

  public void registerFcmToken(FcmTokenRegisterRequest request, Long memberId) {
    Member memberRef = memberService.getRefOrThrow(memberId);
    FcmToken fcmToken = fcmTokenService.create(request, memberRef);
    if (!fcmTokenService.isExist(fcmToken)) {
      fcmTokenService.save(fcmToken);
    }
  }
}
