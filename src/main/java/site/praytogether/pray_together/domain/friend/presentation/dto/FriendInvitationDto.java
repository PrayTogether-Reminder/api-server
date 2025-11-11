package site.praytogether.pray_together.domain.friend.presentation.dto;

import lombok.Builder;

@Builder
public record FriendInvitationDto(Long invitationId, String senderName) {}
