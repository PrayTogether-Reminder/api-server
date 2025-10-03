package site.praytogether.pray_together.domain.invitation.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.praytogether.pray_together.domain.invitation.domain.Invitation;
import site.praytogether.pray_together.domain.invitation.domain.InvitationInfo;
import site.praytogether.pray_together.domain.invitation.domain.InvitationStatus;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

  Optional<Invitation> findByInvitee_IdAndId(Long memberId, Long id);

  @Query(
      """
        SELECT new site.praytogether.pray_together.domain.invitation.domain.InvitationInfo(
        i.id,i.inviterName,r.name,r.description,i.createdTime
        )
        FROM Invitation i
        JOIN i.invitee m
        JOIN i.room r
        WHERE i.invitee.id = :memberId AND i.status = :status
        ORDER BY i.createdTime ASC
""")
  List<InvitationInfo> findInfosByMemberId(Long memberId, InvitationStatus status);
}
