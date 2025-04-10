package site.praytogether.pray_together.domain.invitation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.praytogether.pray_together.domain.invitation.model.Invitation;
import site.praytogether.pray_together.domain.invitation.model.InvitationInfo;
import site.praytogether.pray_together.domain.invitation.model.InvitationStatus;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

  @Query(
      """
        SELECT new site.praytogether.pray_together.domain.invitation.model.InvitationInfo(
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
