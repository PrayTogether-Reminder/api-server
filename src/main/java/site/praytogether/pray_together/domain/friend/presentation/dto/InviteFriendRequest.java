package site.praytogether.pray_together.domain.friend.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record InviteFriendRequest(@NotBlank(message = "친구 요청을 보낼 이메일을 적어주세요.") String inviteeEmail) {

}
