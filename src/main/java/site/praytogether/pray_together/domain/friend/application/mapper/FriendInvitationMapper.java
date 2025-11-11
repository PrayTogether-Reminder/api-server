package site.praytogether.pray_together.domain.friend.application.mapper;

import java.util.List;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;
import site.praytogether.pray_together.domain.friend.presentation.dto.FetchReceivedInvitationResponse;
import site.praytogether.pray_together.domain.friend.presentation.dto.FriendInvitationDto;

public class FriendInvitationMapper {

  public static FetchReceivedInvitationResponse toFetchReceivedInvitationResponse(List<FriendInvitation> invitations) {
    List<FriendInvitationDto> friendInvitationDtos = invitations.stream()
        .map(FriendInvitationMapper::toFriendInvitationDto)
        .toList();
    
    return FetchReceivedInvitationResponse.builder()
        .friendInvitations(friendInvitationDtos)
        .build();
  }

  private static FriendInvitationDto toFriendInvitationDto(FriendInvitation invitation) {
    return FriendInvitationDto.builder()
        .invitationId(invitation.getId())
        .senderName(invitation.getSender().getName())
        .build();
  }
}