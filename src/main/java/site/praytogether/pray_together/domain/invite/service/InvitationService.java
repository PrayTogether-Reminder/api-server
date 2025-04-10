package site.praytogether.pray_together.domain.invite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.invite.repository.InvitationRepository;

@Service
@RequiredArgsConstructor
public class InvitationService {
  private final InvitationRepository invitationRepository;
}
