package site.praytogether.pray_together.domain.fcm_token.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.fcm_token.dto.FcmTokenRegisterRequest;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.fcm_token.repository.FcmTokenRepository;
import site.praytogether.pray_together.domain.member.model.Member;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {
  private final FcmTokenRepository fcmTokenRepository;

  public FcmToken create(FcmTokenRegisterRequest request, Member member) {
    return FcmToken.create(request, member);
  }

  public FcmToken save(FcmToken fcmToken) {
    return fcmTokenRepository.save(fcmToken);
  }

  public boolean isExist(FcmToken fcmToken) {
    return fcmTokenRepository.existsByTokenAndMember(fcmToken.getToken(), fcmToken.getMember());
  }

  public List<FcmToken> fetchTokensByMemberIds(List<Long> memberIds) {
    return fcmTokenRepository.findByMemberIds(memberIds);
  }
}
