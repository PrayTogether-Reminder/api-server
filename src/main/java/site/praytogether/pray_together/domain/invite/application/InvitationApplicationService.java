package site.praytogether.pray_together.domain.invite.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.invite.service.InvitationService;

@Service
@RequiredArgsConstructor
public class InvitationApplicationService {
  private final InvitationService invitationService;
}
