package site.praytogether.pray_together.domain.friend.presentation.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record FetchReceivedInvitationResponse(List<FriendInvitationDto> friendInvitations) {}
