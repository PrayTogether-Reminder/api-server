package site.praytogether.pray_together.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.notification.dto.FcmTokenRegisterRequest;
import site.praytogether.pray_together.domain.notification.model.FcmToken;
import site.praytogether.pray_together.domain.notification.repository.FcmTokenRepository;

@Service
@RequiredArgsConstructor
public class FcmService {
  private final FcmTokenRepository fcmTokenRepository;

  public FcmToken create(FcmTokenRegisterRequest request, Member member) {
    return FcmToken.create(request, member);
  }

  public FcmToken save(FcmToken fcmToken) {
    return fcmTokenRepository.save(fcmToken);
  }
}
