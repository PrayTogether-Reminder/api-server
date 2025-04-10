package site.praytogether.pray_together.domain.invite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.invite.model.Invitation;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {}
