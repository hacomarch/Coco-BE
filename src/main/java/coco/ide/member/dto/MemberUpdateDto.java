package coco.ide.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberUpdateDto {
    private String nickname;
    private String password;
    private Long memberId;

    public MemberUpdateDto() {}

    public MemberUpdateDto(String nickname, String password, Long memberId) {
        this.nickname = nickname;
        this.password = password;
        this.memberId = memberId;
    }

}