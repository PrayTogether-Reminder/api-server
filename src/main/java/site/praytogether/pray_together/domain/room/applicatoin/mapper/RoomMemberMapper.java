package site.praytogether.pray_together.domain.room.applicatoin.mapper;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.member.model.RoomMember;
import site.praytogether.pray_together.domain.room.dto.RoomMemberDto;
import site.praytogether.pray_together.domain.room.dto.RoomMemberResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomMemberMapper {

  public static RoomMemberDto toDto(RoomMember roomMember) {

    return RoomMemberDto.builder()
        .id(roomMember.getId())
        .name(roomMember.getName())
        .phoneNumberSuffix(roomMember.getPhoneNumberSuffix())
        .build();
  }

  public static RoomMemberResponse toResponse(List<RoomMember> roomMembers) {
    List<RoomMemberDto> dtos = roomMembers.stream().map(RoomMemberMapper::toDto).toList();
    return new RoomMemberResponse(dtos);
  }
}
