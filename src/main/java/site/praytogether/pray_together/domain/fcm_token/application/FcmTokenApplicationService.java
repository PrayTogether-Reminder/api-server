package site.praytogether.pray_together.domain.fcm_token.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.fcm_token.dto.FcmTokenDeleteRequest;
import site.praytogether.pray_together.domain.fcm_token.dto.FcmTokenRegisterRequest;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.fcm_token.service.FcmTokenService;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenApplicationService {

  private final FcmTokenService fcmTokenService;
  private final MemberService memberService;

  public void registerFcmToken(FcmTokenRegisterRequest request, Long memberId) {
    Member memberRef = memberService.getRefOrThrow(memberId);
    FcmToken fcmToken = fcmTokenService.create(request, memberRef);
    fcmTokenService.deleteByMemberId(memberRef.getId());
    fcmTokenService.save(fcmToken);
  }

  public void deleteFcmToken(FcmTokenDeleteRequest request,Long memberId) {
    fcmTokenService.deleteByToken(request.getFcmToken(),memberId);
  }
}
