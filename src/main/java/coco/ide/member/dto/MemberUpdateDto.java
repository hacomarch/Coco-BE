package coco.ide.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberUpdateDto {
    private String nickname;
    private String password;

    public MemberUpdateDto() {}

    public MemberUpdateDto(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

}